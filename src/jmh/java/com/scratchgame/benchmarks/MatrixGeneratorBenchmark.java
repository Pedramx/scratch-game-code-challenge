package com.scratchgame.benchmarks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scratchgame.model.Configuration;
import com.scratchgame.service.MatrixGenerator;
import org.openjdk.jmh.annotations.*;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class MatrixGeneratorBenchmark {

    private MatrixGenerator generator;

    @Setup(Level.Trial)
    public void setUp() throws Exception {
        // Load config from resources
        ObjectMapper mapper = new ObjectMapper();
        InputStream configStream = getClass().getClassLoader().getResourceAsStream("config.json");
        if (configStream == null) {
            throw new RuntimeException("config.json not found in resources");
        }
        Configuration config = mapper.readValue(configStream, Configuration.class);
        generator = new MatrixGenerator(config);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public String[][] testMatrixGeneration() {
        return generator.generate();
    }
}
