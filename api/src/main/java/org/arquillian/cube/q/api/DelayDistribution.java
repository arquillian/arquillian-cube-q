package org.arquillian.cube.q.api;

/**
 * Base interface for implementing any kind of mathematical distribution
 */
public interface DelayDistribution {

    long calculate();

}
