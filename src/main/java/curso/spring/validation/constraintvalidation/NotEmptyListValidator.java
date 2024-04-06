package curso.spring.validation.constraintvalidation;

import curso.spring.dto.ItemPedidoDTO;
import curso.spring.validation.NotEmptyList;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class NotEmptyListValidator implements ConstraintValidator<NotEmptyList, List<ItemPedidoDTO>> {
    @Override
    public boolean isValid(List list, ConstraintValidatorContext constraintValidatorContext) {
        return list != null && !list.isEmpty();
    }

}
