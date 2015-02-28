# Passambler
Passambler is a simple scripting language.

Some packages aren't finished yet, but the language is usable.

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

For more examples, see the `examples` directory.

## Wiki
The wiki contains all the documentation and the [getting started guide](https://github.com/raoulvdberge/Passambler/wiki/Getting-Started).

## License
GPL v2 license (&copy; 2015 Raoul Van den Berge)
