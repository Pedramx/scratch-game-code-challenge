## Building the Project

To build the project, run the following command in the project root directory:

```
mvn clean package
```

This will:
1. Clean the target directory
2. Compile the source code
3. Run the tests
4. Create an executable JAR with dependencies

## Running the Application

1. Ensure your `config.json` file is in the same directory as the JAR file
2. Run the application using:

```
java -jar scratch-game-code-challenge-1.0-SNAPSHOT-jar-with-dependencies.jar --config config.json --betting-amount 100
```

### Command Line Arguments

- `--config`: Path to the configuration file (required)
- `--betting-amount`: Amount to bet for each game (required)

## Configuration File

The `config.json` file defines the game rules, symbols, and winning combinations. Make sure it's placed in the same directory as the JAR file before running the application.

Example config.json structure:

```
{
    "symbols": [
        {
            "name": "A",
            "reward_multiplier": 50,
            "type": "standard"
        },
        {
            "name": "B",
            "reward_multiplier": 25,
            "type": "bonus"
        }
    ],
    "win_combinations": [
        {
            "count": 3,
            "reward_multiplier": 10
        }
    ],
    "probability_matrix": [
        [0.2, 0.3, 0.5],
        [0.4, 0.3, 0.3],
        [0.3, 0.4, 0.3]
    ]
}
```

## Testing

### Unit Tests

Run unit tests with:
```
mvn test
```

### Integration Tests

Run integration tests with:
```
mvn verify -P integration-tests
```

### Performance Tests

Run performance tests with:
```
mvn verify -P performance-tests
```

The performance tests include:
- Throughput testing (games per second)
- Memory usage analysis
- Response time measurements

### Test Coverage

Generate test coverage report:
```
mvn jacoco:report
```
The coverage report will be available at `target/site/jacoco/index.html`

## Development

### Building Without Tests

If you want to skip tests during build:
```
mvn clean package -DskipTests
```

### Debug Mode

Run the application in debug mode:
```
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar scratch-game-code-challenge-1.0-SNAPSHOT-jar-with-dependencies.jar --config config.json --betting-amount 100
```

### Profiling

To run with JVM profiling:
```
java -XX:+FlightRecorder -XX:StartFlightRecording=duration=60s,filename=recording.jfr -jar scratch-game-code-challenge-1.0-SNAPSHOT-jar-with-dependencies.jar --config config.json --betting-amount 100
```

## Performance Optimization Tips

1. **Memory Usage**
   - Configure appropriate JVM heap size: `-Xmx2g -Xms2g`
   - Use garbage collector flags for better performance: `-XX:+UseG1GC`

2. **Throughput**
   - For batch processing, use: `--batch-size 1000`
   - Enable parallel processing: `--parallel-execution true`

## Troubleshooting

Common issues and solutions:

1. **FileNotFoundException**
   - Ensure the config.json file is in the same directory as the JAR file
   - Check file permissions

2. **Invalid JSON Format**
   - Verify your config.json follows the correct format
   - Use a JSON validator

3. **Java Version Error**
   - Make sure you're using Java 11 or higher
   - Check JAVA_HOME environment variable

4. **Out of Memory Error**
   - Increase JVM heap size: `-Xmx4g`
   - Monitor memory usage with JConsole

## Monitoring

### JMX Monitoring

Enable JMX monitoring:
```
java -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -jar scratch-game-code-challenge-1.0-SNAPSHOT-jar-with-dependencies.jar --config config.json --betting-amount 100
```

### Metrics

The application exposes the following metrics:
- Games played per second
- Win/loss ratio
- Average payout
- Memory usage
- Response time percentiles

## Contributing

1. Fork the repository
2. Create your feature branch
3. Write tests for your changes
4. Ensure all tests pass
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details

## Version History

- 1.0.0: Initial release
- 1.0.1: Performance optimizations
- 1.1.0: Added parallel processing support
```