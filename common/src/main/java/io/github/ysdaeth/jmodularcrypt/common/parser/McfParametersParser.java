package io.github.ysdaeth.jmodularcrypt.common.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parser is designed to create Modular Crypt Format (MCF) parameters string output
 * MCF parameters format are sections delimited by the ',' sign. and between every
 * key and value there is '=' sign.
 * <p>Example:</p>
 * {@code a=1,bc=d,e=fg}
 */
public class McfParametersParser implements Parser{

    /**
     * Concatenate sections values with a ',' sign at the beginning of each section.
     * Concatenate section key and value with '=' sign.
     * <p>Example composed</p>
     * {@code a=1,bc=d,e=fg}
     * @param sections section of Modular Crypt Format string representation
     * @return concatenated string separated with ',' sign.
     */
    @Override
    public String compose(Section[] sections) {
        List<String> pairs =new ArrayList<>(sections.length);
        for (Section section : sections) {
            validateSection(section);
            String pair = section.key() + '=' + section.value();
            pairs.add(pair);
        }
        return String.join(",",pairs);
    }


    /**
     * Create sections out of composed MCF parameter format string value.
     * <p>Example composed:</p>
     * {@code a=1,bc=d,e=fg}
     * @param composed composed string value
     * @return parsed sections of that format
     */
    @Override
    public Section[] parse(String composed) {
        String[] parts = Arrays.stream(composed.split(","))
                .filter(s->!s.isBlank())
                .toArray(String[]::new);
        Section[] sections = new Section[parts.length];

        for(int i=0; i< parts.length; i++){
            String pair = parts[i];
            int equalIndex = pair.indexOf('=');
            String key = pair.substring(0, equalIndex);
            String value = pair.substring(equalIndex + 1);
            sections[i] = new Section(key,value);
        }
        return sections;
    }

     /** Validate if specified section of MCF is valid value
     * @param section section of the composed MCF value
     * @return the same value
     */
    private static Section validateSection(Section section){
        if(section == null) throw new IllegalArgumentException("Section cannot be null");
        String key = section.key();
        String value  =section.value();
        if(key == null || key.isBlank()){
            throw new IllegalArgumentException("Section key cannot be null or blank");
        }
        if(hasSign(key,'=') || hasSign(key,',') ){
            throw new IllegalArgumentException("Section key cannot contain ',' or '=' sign" );
        }
        if(value == null || value.isBlank()){
            throw new IllegalArgumentException("Section value cannot be null or blank");
        }
        if(hasSign(value,',')){
            throw new IllegalArgumentException("Section value cannot contain ',' sign" );
        }
        return section;
    }

    private static boolean hasSign(String section,char sign){
        char[] chars = section.toCharArray();
        for(char c: chars){
            if(c == sign) return true;
        }
        return false;
    }
}
