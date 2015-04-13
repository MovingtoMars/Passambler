# Passambler
Passambler is a simple general purpose language.

It isn't finished yet, but the language is usable.

## Examples
A simple Hello World program looks like this:

```
using('std.Writeln');

fn hello(name) {
    Writeln('Hello ' + name + '!');
}

hello('World');
```

And a FizzBuzz program is written like this:
```
using('std.Writeln');

for (x : 0..100) {
    if ((x % 15) == 0) {
        Writeln('FizzBuzz');
    } elseif ((x % 3) == 0) {
        Writeln('Fizz');
    } elseif ((x % 5) == 0) {
        Writeln('Buzz');
    }
}
```

Passambler also supports typehinting. See this example:
```
fn sum(num a, num b) {
    return a + b;
}

sum(1, 1);
sum('Another type!', 1);
```
The second `sum` call would cause a type mismatch error.