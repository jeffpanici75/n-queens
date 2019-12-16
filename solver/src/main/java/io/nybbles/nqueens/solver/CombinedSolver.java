package io.nybbles.nqueens.solver;

import io.nybbles.common.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.time.Duration;
import java.util.ArrayList;

import static io.nybbles.common.BitHelpers.bitReverse;

// Native memory layout:
//
//                     each solution
//             +---------------------------+
//                  N    Queen           N
// Count N Pad Row ...   ID Y X ... Row ...
//   4   1  3   4        1  1 1      4
//
//
public class CombinedSolver {
    private static final Logger s_logger = LoggerFactory.getLogger(CombinedSolver.class);
    private final ThreeInLineDetector _threeInLineDetector = new ThreeInLineDetector();
    private final ArrayList<CombinedSolution> _solutions = new ArrayList<>();
    private final StopWatch _stopWatch = new StopWatch();
    private long _bufferAddress;

    private static final Unsafe s_unsafe;
    static {
        try {
            var field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            s_unsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final class SolutionData {
        private final long _solutionDataOffset;
        private final long _queensDataOffset;
        private Boolean _mirror;

        private void populateDetectorQueens() {
            var queens = _threeInLineDetector.getQueens();
            var offset = _queensDataOffset;
            for (var i = 0; i < queens.length; ++i) {
                queens[i] = new Queen(
                        s_unsafe.getByte(offset),
                        s_unsafe.getByte(offset + 1),
                        s_unsafe.getByte(offset + 2));
                offset += 3;
            }
        }

        public SolutionData(long solutionDataOffset, long queensDataOffset) {
            _solutionDataOffset = solutionDataOffset;
            _queensDataOffset = queensDataOffset;
        }

        public byte getQueenId(byte index) {
            return s_unsafe.getByte((_queensDataOffset + (index * 3)));
        }

        public byte getQueenColumn(byte index) {
            return s_unsafe.getByte((_queensDataOffset + (index * 3)) + 2);
        }

        public byte getQueenRow(byte index) {
            return s_unsafe.getByte((_queensDataOffset + (index * 3)) + 1);
        }

        public void update(boolean mirror) {
            if (_mirror != null && _mirror == mirror)
                return;
            _mirror = mirror;
            byte y = 0;
            byte queenIndex = 0;
            final var n = s_unsafe.getByte(_bufferAddress + 4);
            var solutionDataOffset = _solutionDataOffset;
            for (var i = 0; i < n; ++i) {
                var row = s_unsafe.getInt(solutionDataOffset);
                var effectiveRow = mirror ? bitReverse(row, n) : row;
                var mask = 1 << (n - 1);
                for (byte x = 0; x < n; x++) {
                    if ((effectiveRow & mask) == mask) {
                        var queenDataOffset = _queensDataOffset + (queenIndex * 3);
                        s_unsafe.putByte(queenDataOffset, queenIndex);
                        s_unsafe.putByte(queenDataOffset + 1, y);
                        s_unsafe.putByte(queenDataOffset + 2, x);
                        ++queenIndex;
                        break;
                    }
                    mask >>= 1;
                }
                solutionDataOffset += 4;
                ++y;
            }
        }

        public boolean hasThreeInLine() {
            final var n = s_unsafe.getByte(_bufferAddress + 4);
            populateDetectorQueens();
            return _threeInLineDetector.areCollinear(n);
        }

        public ThreeInLineDetector.Result getThreeInLineResult() {
            final var n = s_unsafe.getByte(_bufferAddress + 4);
            populateDetectorQueens();
            return _threeInLineDetector.areCollinearWithResult(n);
        }
    }

    public static final class CombinedSolution {
        public CombinedSolution(SolutionData data) {
            this.data = data;
            this.hasStraightLines = data.hasThreeInLine();
        }
        public final SolutionData data;
        public final boolean hasStraightLines;
    }

    public CombinedSolver() {
    }

    public int solve(int n) {
        _stopWatch.start();
        _solutions.clear();

        if (_bufferAddress != 0) {
            s_logger.info(
                    "free native memory buffer: {}",
                    String.format("%016X", _bufferAddress));
            s_unsafe.freeMemory(_bufferAddress);
        }

        var context = new Context(n);
        var solver = new BitwiseSymmetrySolver(context);
        var solutions = solver.solve();
        _solutions.ensureCapacity(solutions.size());

        s_logger.info("n queens solver completed.");

        long bufferSize = 8 + ((long) solutions.size() * (n * 8));
        s_logger.info(
                "allocate native memory buffer of size: {}",
                String.format("%016X", bufferSize));
        _bufferAddress = s_unsafe.allocateMemory(bufferSize);

        s_unsafe.putInt(_bufferAddress, solutions.size());
        s_unsafe.putByte(_bufferAddress + 4, (byte) n);
        s_unsafe.putByte(_bufferAddress + 5, (byte) 0);
        s_unsafe.putByte(_bufferAddress + 6, (byte) 0);
        s_unsafe.putByte(_bufferAddress + 7, (byte) 0);

        var position = _bufferAddress + 8;
        for (var solution : solutions) {
            var solutionDataOffset = position;
            for (var row : solution) {
                s_unsafe.putInt(position, row);
                position += 4;
            }

            var queensDataOffset = position;
            for (var i = 0; i < n; i++) {
                s_unsafe.putInt(position, (byte) i);
                s_unsafe.putInt(position + 1, 0);
                s_unsafe.putInt(position + 2, 0);
                position += 3;
            }

            var data = new SolutionData(solutionDataOffset, queensDataOffset);
            data.update(false);

            _solutions.add(new CombinedSolution(data));
        }

        _stopWatch.stop();

        return context.count;
    }

    public Duration getExecutionTime() {
        return _stopWatch.getElapsedTime();
    }

    public ArrayList<CombinedSolution> getSolutions() {
        return _solutions;
    }
}
