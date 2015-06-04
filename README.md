# Passambler [![Join the chat at https://gitter.im/raoulvdberge/Passambler](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/raoulvdberge/Passambler?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Passambler is a simple general purpose programming language.

## Examples
A simple Hello World program looks like this:
```
writeln("Hello World")
```

And a FizzBuzz program is written like this:
```
for x in 0...100 {
    if x % 15 == 0 {
        writeln("FizzBuzz")
    } elseif x % 3 == 0 {
        writeln("Fizz")
    } elseif x % 5 == 0 {
        writeln("Buzz")
    }
}
```
For more examples, see the `examples` directory.

**If you want to see more examples of how the language works, check out the tests in the `tests` directory.**

## Building
Clone the repository and run Maven.
```
$ git clone https://github.com/raoulvdberge/Passambler.git
$ cd Passambler
$ mvn install
```
You will find the jar file and the REPL in the `target` directory.

Java 8 is required to build and run the language.

## Contributing
Contributions are always welcome! You can contribute to Passambler in following ways:

- Reporting and/or fixing bugs
- Implementing new features
- Comment on issues, we need your voice!

## License
MIT license
