package de.rwth.idsg.barti.web.config;

import de.rwth.idsg.barti.sam.Aztec;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class PictureProxyConverter extends AbstractHttpMessageConverter<PictureProxy> {
    public PictureProxyConverter() {
        super(MediaType.ALL);
    }

    @Override
    protected boolean supports(final Class<?> clazz) {
        return PictureProxy.class.isAssignableFrom(clazz);
    }

    @Override
    protected boolean canRead(final MediaType mediaType) {
        return false;
    }

    @Override
    protected PictureProxy readInternal(final Class<? extends PictureProxy> clazz, final HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException("PictureProxyConverter is only able to WRITE");
    }

    @Override
    protected void writeInternal(final PictureProxy pictureProxy, final HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        Aztec.createPNG(pictureProxy.getSignature(), outputMessage.getBody());
    }
}
