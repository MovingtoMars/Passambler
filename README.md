# Passambler
Passambler is a simple scripting language.

The language isn't finished yet, so stay tuned.

## Examples
A simple Hello World program looks like this:

```
import 'std';

writeln('Hello World!');
```

A FizzBuzz program is written like this:
```
import 'std';

for (x : 0..100) {
    if ((x % 15) == 0) {
        writeln('FizzBuzz');
    } elseif ((x % 3) == 0) {
        writeln('Fizz');
    } elseif ((x % 5) == 0) {
        writeln('Buzz');
    }
}
```

## License
GPL v2 license (&copy; 2015 Raoul Van den Berge)
