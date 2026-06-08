package hello.itemservice.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import static org.assertj.core.api.Assertions.assertThat;

class MessageCodesResolverTest {

    MessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();

    // BindingResult에는 new FieldError과 new ObjectError을 매개변수로 넣었던 것을 기억하자.

    @Test
    // BindingResult.reject()의 내부 동작
    void messageCodesResolverObject() {
        String[] messageCodes = messageCodesResolver.resolveMessageCodes("required", "item");
        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode); // 출력 결과 : "required.item", "required"
                                                                // 즉, BindingResult.reject() 실행 시 MessageCodesResolver가 new ObjectError(new String[]{"required.item", "required"})를
                                                                // 생성하는 것이며, 우선순위에 따라 required.item를 먼저 찾고, 그게 없으면 required를 찾는다.
                                                                // (더 정확히는 new ObjectError의 codes 인자 부분에 messageCodes를 자동 삽입한다)
        }
        assertThat(messageCodes).contains("required.item", "required");
    }

    @Test
    // BindingResult.rejectValue()의 내부 동작
    void messageCodesResolverField() {
        String[] messageCodes = messageCodesResolver.resolveMessageCodes("required", "item", "itemName", String.class);
        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode); // 출력 결과 : "required.item.itemName", "required.itemName", required.java.lang.String, "required"
        }                                                       // 즉, BindingResult.rejectValue() 실행 시 MessageCodesResolver가 new FieldError(new String[]{"required.item.itemName", "required.item", "java.lang.String", "required"})를
                                                                // 생성하는 것이며, 우선순위에 따라 required.item.itemName를 먼저 찾고, 그게 없으면 그 다음 우선 순위들을 찾는다.
                                                                // (더 정확히는 new FieldError의 codes 인자 부분에 messageCodes를 자동 삽입한다)
        assertThat(messageCodes).contains("required.item.itemName", "required.itemName", "required.java.lang.String", "required");
    }








}
