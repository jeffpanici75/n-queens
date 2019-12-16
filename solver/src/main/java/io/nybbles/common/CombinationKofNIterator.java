package io.nybbles.common;

import java.util.*;

public class CombinationKofNIterator<T> implements Iterator<List<T>> {
    private List<T> _currentCombination;
    private final List<T> _list;
    private final int _lengthN;
    private final int _lengthK;
    private int[] _bitVector;
    private int _endIndex;

    private static <T> void setValue(List<T> list, int index, T value) {
        if (index < list.size())
            list.set(index, value);
        else
            list.add(index, value);
    }

    public CombinationKofNIterator(T[] items, int k, int n) {
        _list = new ArrayList<>();
        _list.addAll(Arrays.asList(items).subList(0, n));
        _lengthK = k;
        _lengthN = n;
        _bitVector = new int[_lengthK + 1];
        for (var i = 0; i <= _lengthK; i++)
            _bitVector[i] = i;
        if (_lengthN > 0)
            _endIndex = 1;
        _currentCombination = new ArrayList<>(k);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> next() {
        for (int i = 1; i <= _lengthK; i++) {
            int index = _bitVector[i] - 1;
            if (_list.size() > 0)
                setValue(_currentCombination, i - 1, _list.get(index));
        }

        _endIndex = _lengthK;

        while (_bitVector[_endIndex] == _lengthN - _lengthK + _endIndex) {
            _endIndex--;
            if (_endIndex == 0)
                break;
        }
        _bitVector[_endIndex]++;
        for (int i = _endIndex + 1; i <= _lengthK; i++)
            _bitVector[i] = _bitVector[i - 1] + 1;

        return new ArrayList<>(_currentCombination);
    }

    @Override
    public boolean hasNext() {
        return !((_endIndex == 0) || (_lengthK > _lengthN));
    }
}
