package hello.itemservice.validation;

import hello.itemservice.domain.item.Item;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation; // 자바 표준
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory; // 자바 표준
import org.junit.jupiter.api.Test;

import java.util.Set;

public class BeanValidationTest {

@Test
void beanValidation() {

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory(); // 자바 표준에서 제공하는 구현체
    Validator validator = factory.getValidator();

    Item item = new Item();
    item.setItemName(" ");
    item.setPrice(0);
    item.setQuantity(10000000);

    Set<ConstraintViolation<Item>> validate = validator.validate(item);
    for (ConstraintViolation<Item> violation : validate) {
        System.out.println("violation = " + violation);
        System.out.println("violation.getMessage() = " + violation.getMessage());
    }

}


}
