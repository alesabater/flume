/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sigis.gtrserializer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.serialization.EventSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author asabater
 */
public class GTRSerializer implements EventSerializer {

private final static Logger logger = LoggerFactory.getLogger(GTRSerializer.class);

    
    /* PARAMS TO RECEIVE FROM CONFIG. FILE */
    public static final String DELIMETER = "delimeter";
    public static final String REGEX = "regex";
    
    /* DEFAULTS FOR CONFIG PARAMS */
    private final List<String> DEFAULT_DELIMITER = Arrays.asList(",", "\t");
    private final String DEFAULT_REGEX = "ARRAY\\[(.*)\\],ARRAY\\[(.*)\\],(.*),(.*)\\)";
    
    /* VARIABLES */
    private final String delimeter;
    private final Pattern regex;
    private final OutputStream out;
    private Map<Integer, ByteBuffer > orderIndexer;
    
    private GTRSerializer(OutputStream out, Context ctx) {
        
        Map<String,String> delimeter2 = ctx.getParameters();
        
        this.delimeter = ctx.getString(DELIMETER, DEFAULT_DELIMITER.get(0));
        
        if (!DEFAULT_DELIMITER.contains(delimeter)){
                logger.warn("Unsupported delimeter format" + delimeter + ", using default instead");
        }
        this.regex = Pattern.compile(ctx.getString(REGEX, DEFAULT_REGEX));
        this.out = out;
        orderIndexer = new HashMap<Integer, ByteBuffer>();
    }
    
    @Override
    public void afterCreate() throws IOException {
  
    }

    @Override
    public void afterReopen() throws IOException {
        
    }
    
    @Override
    public void flush() throws IOException {
        
    }

    @Override
    public void beforeClose() throws IOException {
        
    }

    @Override
    public boolean supportsReopen() {
        return true;
    }

    @Override
    public void write(Event event) throws IOException {
        
        String message = new String(event.getBody(), Charsets.UTF_8);
        System.out.println(message);
        String[] eventFields = message.split("\\t");
        
        Matcher matcher = regex.matcher(eventFields[4]);
        
        if (matcher.find()) {
        
        for(int i=0; i<4; i++){
            out.write(eventFields[i].getBytes());
            out.write(delimeter.getBytes());
        }
        
        int groupIndex;
        int totalGroups = matcher.groupCount();
        for (int i = 0, count = totalGroups; i < count; i++) {
                groupIndex = i + 1;
                orderIndexer.put(i, ByteBuffer.wrap(matcher.group(groupIndex).getBytes()));
        }
        
        int i = 1;
        for(Integer key : orderIndexer.keySet()){
            out.write(orderIndexer.get(key).array());
            if (i < totalGroups){
                    out.write(delimeter.getBytes());
            }
            i++;
        }
        
        out.write('\n');
        }
        
        else {
            logger.warn("Message skipped, no regex match: " + eventFields[4]);
        }
        
        
        
    }
    
    public static class Builder implements EventSerializer.Builder {
            @Override
            public EventSerializer build(Context context, OutputStream out) {
                    GTRSerializer s = new GTRSerializer(out, context);
                    return s;
            }
    }

    
}




