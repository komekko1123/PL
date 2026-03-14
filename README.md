# OurScheme Interpreter 
A simplified **Scheme interpreter implemented in C++**, developed as the **final project of the Programming Languages course (CYCU-PL-114-2)** at Chung Yuan Christian University.

The interpreter reads **S-expressions**, parses them, evaluates them, and prints the results.
## Features

Core components implemented:

- Lexer / Scanner (tokenization)
- S-expression Parser
- Pretty Printer
- Evaluation Engine
- Environment binding

Supported operations:

- List: `cons`, `car`, `cdr`, `list`
- Predicates: `atom?`, `pair?`, `list?`, `null?`
- Arithmetic: `+`, `-`, `*`, `/`
- Logic: `and`, `or`, `not`
- Comparison: `>`, `<`, `>=`, `<=`, `=`
- Binding: `define`, `set!`
- Control: `if`, `cond`, `begin`
- Functions: `lambda`, `let`
- I/O: `read`, `write`, `display-string`, `newline`

---


## Example

```
Welcome to OurScheme!

> (define a 5)
a defined

> (+ a 3)
8

> (cons 3 '(4 5))
( 3
  4
  5
)
```

### Nested evaluation

```
> (car (cdr '(10 20 30 40)))
20
```

### Nested list construction

```
> (cons '(1 2) (list 3 4))
( ( 1
    2
  )
  3
  4
)
```

### Lambda function

```
> ((lambda (x) (* x x)) 6)
36
```

### let local binding

```
> (let ((x 3)
        (y 4))
    (+ x y))
7
```

### User-defined function

```
> (define (square x) (* x x))
square defined

> (square 5)
25
```

### Recursive function (factorial)

```
> (define (fact n)
    (if (= n 0)
        1
        (* n (fact (- n 1)))))
fact defined

> (fact 5)
120
```

### Quote expression

```
> '(1 (2 3) 4)
( 1
  ( 2
    3
  )
  4
)
```

### Error: unbound symbol

```
> (+ x 3)
ERROR (unbound symbol) : x
```

### Error: applying non-function

```
> (3 4 5)
ERROR (attempt to apply non-function) : 3
```

### Error: incorrect argument type

```
> (car 3)
ERROR (car with incorrect argument type) : 3
```



## Build

Compile

```
g++ PJ4.cpp -o OurScheme
```

Run

```
./OurScheme
```

---
