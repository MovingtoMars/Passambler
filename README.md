# Passambler
Passambler is a simple general purpose language.

## Examples
A simple Hello World program looks like this:
```
writeln('Hello World');
```

And a FizzBuzz program is written like this:
```
for x : 0..100 {
    if x % 15 == 0 {
        writeln('FizzBuzz');
    } elseif x % 3 == 0 {
        writeln('Fizz');
    } elseif x % 5 == 0 {
        writeln('Buzz');
    }
}
```

## Building
Clone the repository and run Maven.
```
mvn install
```
You can find the jar file in the `target` directory.

## License
MIT license