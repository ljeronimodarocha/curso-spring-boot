package curso.spring;

import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@NoArgsConstructor
public class MessageUtil {

    private MessageSource messageSource;

    public MessageUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String message) {
        return this.getMessage(message, null, Locale.getDefault());
    }

    public String getMessageWithArgs(String message, Object[] args) {
        return this.getMessage(message, args, Locale.getDefault());
    }

    private String getMessage(String message, Object[] args, Locale locale) {
        return messageSource.getMessage(message, args, locale);
    }


}
