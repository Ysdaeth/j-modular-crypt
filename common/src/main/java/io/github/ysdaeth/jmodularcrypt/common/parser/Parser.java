package io.github.ysdaeth.jmodularcrypt.common.parser;

/**
 * Parse sections that represents key value pair to string, and string to key value pairs.
 * String value structure depends on the parsing algorithm. This interface should be used for
 * parsing simple line structures.
 */
public interface Parser {

    /**
     * Create composed string from given key value pairs
     * @param sections key value pairs as strings
     * @return parsed to string
     */
    String compose(Section[] sections);

    /**
     * Parse string to sections which are key value pairs of string type
     * @param computed output of this parser
     * @return key value pairs
     */
    Section[] parse(String computed);
}
