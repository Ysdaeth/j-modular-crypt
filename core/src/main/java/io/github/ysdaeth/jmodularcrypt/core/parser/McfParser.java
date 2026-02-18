package io.github.ysdaeth.jmodularcrypt.core.parser;

import java.util.Arrays;

/**
 * Parser is designed to create Modular Crypt Format (MCF) string output
 * MCF is format where sections delimited by the '$' sign.
 * This class is stateless and thread-safe.
 * ModuleCrypt format section does not contain key like JSON format, only values instead.
 * If section names should be present for example creating parameters then use {@link McfParametersParser}
 */
public class McfParser implements Parser {

    /**
     * Validate if specified section of MCF is valid value
     * @param section section of composed MCF value
     * @return the same value
     */
    private static Section validateSection(Section section){
        if(section == null) throw new IllegalArgumentException("Section must not be null");
        if(section.value().isBlank()) throw new IllegalArgumentException("Section value must not be blank");
        if(section.value().contains("$")) throw new IllegalArgumentException("Section value must not contain $ sign");
        return section;
    }

    /**
     * Concatenate sections values with a '$' sign at the beginning of each value. It will ignore section names.
     * If names are required use {@link McfParametersParser} instead
     * @param sections section of Modular Crypt Format string representation
     * @return concatenated string separated with '$' sign
     */
    @Override
    public String compose(Section[] sections) {
        StringBuilder stringBuilder = new StringBuilder();
        Arrays.stream(sections)
                .map(McfParser::validateSection)
                .forEach(
                        s -> stringBuilder.append("$").append(s.value())
                );
        return stringBuilder.toString();
    }

    /**
     * Return sections from Modular Crypt Format. Due to MCF limitative nature,
     * this will return sections without section names. If section names are required
     * use {@link McfParametersParser} instead when serializing
     * @param composed composed string format produced by this parsed
     * @return sections without section names.
     */
    @Override
    public Section[] parse(String composed) {
        return Arrays.stream(composed.split("\\$"))
                .filter(s->!s.isBlank())
                .map(s->new Section(null,s))
                .map(McfParser::validateSection)
                .toArray(Section[]::new);
    }

}
