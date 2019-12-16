### N Queens and No-three-in-line Solver

This program implements a binary, symmetry aware, backtracking solution to 
the N Queens problem. The algorithm is based primarily on the work in the paper,
["Backtracking Algorithms in MCPL using Bit Patterns and Recursion,"](https://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.51.7113&rep=rep1&type=pdf) 
by Martin Richards.
Code was added to detect mirror image solutions. The paper discussions a recursive
algorithm, but this program implements the solution as an iterative state machine to 
limit call stack depth.
  
In addition, the solver checks for any three queens
which are collinear.  The visualizer marks collinear paths as red, and
all other paths as green.  This part of the algorithm was based on the, 
"No-three-in-line problem," which is discussed [here](https://en.wikipedia.org/wiki/No-three-in-line_problem).

The visualizer allows the user to filter the results based on detection of
collinear queens.

### Screenshots

<div style="justify-content: space-around">
    <div>
        <a href="https://raw.github.com/jeffpanici75/n-queens/master/assets/screenshot-1.png">
            <img src="/assets/screenshot-1.png" width="50%" height="50%" />
        </a>
    </div>
    <div>
        <a href="https://raw.github.com/jeffpanici75/n-queens/master/assets/screenshot-2.png">
            <img src="/assets/screenshot-2.png" width="50%" height="50%" />
        </a>
    </div>
    <div>
        <a href="https://raw.github.com/jeffpanici75/n-queens/master/assets/screenshot-3.png">
            <img src="/assets/screenshot-3.png" width="50%" height="50%" />
        </a>
    </div>
</div>

<div style="justify-content: space-around">
    <div>
        <a href="https://raw.github.com/jeffpanici75/n-queens/master/assets/screenshot-4.png">
            <img src="/assets/screenshot-4.png" width="50%" height="50%" />
        </a>
    </div>
    <div>
        <a href="https://raw.github.com/jeffpanici75/n-queens/master/assets/screenshot-5.png">
            <img src="/assets/screenshot-5.png" width="50%" height="50%" />
        </a>
    </div>
</div>

### Working with the code

I use IntelliJ as my IDE, but the code should work in Eclipse. Gradle is the build system.

The code is built against and requires Java 13 to run. I make use of preview features in the Java language, 
so the `--enable-preview` is required for compiling and running.

