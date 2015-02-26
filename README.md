# Passambler
Passambler is a simple scripting language.

The language isn't finished yet, so stay tuned.

## Examples
A simple Hello World program looks like this:

```
import std.Writeln;

Writeln('Hello World!');
```

A FizzBuzz program is written like this:
```
import std.Writeln;

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

## License
GPL v2 license (&copy; 2015 Raoul Van den Berge)
