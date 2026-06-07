package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    //@PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, // BindingResult는 반드시 검증 대상 매개 변수 뒤에 위치해야 함
                          RedirectAttributes redirectAttributes, Model model) {     // 그래야 Item에 대한 binding 결과가 제대로 들어온다

        // 검증 로직(특정 필드에 대한 검증)
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item","itemName","상품명은 필수값입니다."));
        }
        if(item.getPrice() == null || (item.getPrice() < 1000 || item.getPrice() > 1000000)) {
            bindingResult.addError(new FieldError("item","price","가격은 1000 ~ 1000000까지만 허용됩니다."));
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.addError(new FieldError("item","quantity","수량은 9999개까지만 허용됩니다."));
        }

        // 특정 필드 검증이 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item","가격*수량은 10000 이상이여야 합니다. 현재값 : " + resultPrice));
            }
        }

        // 검증 실패하면 결과 데이터를 사용자에게 그대로 보내주워야 한다.
        if(bindingResult.hasErrors()) {
            log.info("errors={}",bindingResult);
            return "/validation/v2/addForm";
        }

        // 여기서부터는 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    //@PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes, Model model) {

        // 검증 로직(특정 필드에 대한 검증) -> 타입 오류는 @ModelAttribute Item item, BindingResult bindingResult에서 이미 잡는다.
        if (!StringUtils.hasText(item.getItemName())) { // new Field에 들어간 bindingFailure false값은 타입 오류 값은 바인딩 오류가 아니라 필드의 검증 오류라는 뜻이다.
            bindingResult.addError(new FieldError("item","itemName",item.getItemName(),false,null,null,"상품명은 필수값입니다."));
        }
        if(item.getPrice() == null || (item.getPrice() < 1000 || item.getPrice() > 1000000)) {
            bindingResult.addError(new FieldError("item","price",item.getPrice(),false,null,null,"가격은 1000 ~ 1000000까지만 허용됩니다."));
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.addError(new FieldError("item","quantity",item.getQuantity(),false,null,null,"수량은 9999개까지만 허용됩니다."));
        }

        // 특정 필드 검증이 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item",null,null,"가격*수량은 10000 이상이여야 합니다. 현재값 : " + resultPrice));
            }
        }

        // 검증 실패하면 결과 데이터를 사용자에게 그대로 보내주워야 한다.
        if(bindingResult.hasErrors()) {
            log.info("errors={}",bindingResult);
            return "/validation/v2/addForm";
        }

        // 여기서부터는 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    //@PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes, Model model) {

        // 검증 로직(특정 필드에 대한 검증) -> 타입 오류는 @ModelAttribute Item item, BindingResult bindingResult에서 이미 잡는다.
        if (!StringUtils.hasText(item.getItemName())) { // new Field에 들어간 bindingFailure false값은 타입 오류 값은 바인딩 오류가 아니라 필드의 검증 오류라는 뜻이다.
            bindingResult.addError(new FieldError("item","itemName",item.getItemName(),false,new String[]{"required.item.itemName"},null,null));
        }
        if(item.getPrice() == null || (item.getPrice() < 1000 || item.getPrice() > 1000000)) {
            bindingResult.addError(new FieldError("item","price",item.getPrice(),false,new String[]{"range.item.price"},new Object[]{1000,1000000},null));
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.addError(new FieldError("item","quantity",item.getQuantity(),false,new String[]{"max.item.quantity"},new Object[]{9999},null));
        }

        // 특정 필드 검증이 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"},new Object[]{10000,resultPrice},null));
            }
        }

        // 검증 실패하면 결과 데이터를 사용자에게 그대로 보내주워야 한다.
        if(bindingResult.hasErrors()) {
            log.info("errors={}",bindingResult);
            return "/validation/v2/addForm";
        }

        // 여기서부터는 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes, Model model) {
        // BindinsingResult를 Item 바로 뒤에 위치시킴으로 인해, 이미 검증 대상이 Item이라는 정보를 가지고 있다.
        // -> BindingResult가 target 객체를 이미 알고 있음으로, new Field, new ObjectError 없이 검증 오류를 처리할 수가 있다.
        log.info("objectName={}",bindingResult.getObjectName());
        log.info("target={}",bindingResult.getTarget());

        // 검증 로직(특정 필드에 대한 검증) -> 타입 오류는 @ModelAttribute Item item, BindingResult bindingResult에서 이미 잡는다.
        if (!StringUtils.hasText(item.getItemName())) { // new Field에 들어간 bindingFailure false값은 타입 오류 값은 바인딩 오류가 아니라 필드의 검증 오류라는 뜻이다.
            //bindingResult.addError(new FieldError("item","itemName",item.getItemName(),false,new String[]{"required.item.itemName"},null,null));
            bindingResult.rejectValue("itemName","required"); // 결국에는 rejectValue안의 매개변수를 가지고 뒤에서 위 new Field 코드를 자동 작성 해준다.
        }
        if(item.getPrice() == null || (item.getPrice() < 1000 || item.getPrice() > 1000000)) {
            //bindingResult.addError(new FieldError("item","price",item.getPrice(),false,new String[]{"range.item.price"},new Object[]{1000,1000000},null));
            bindingResult.rejectValue("price","range",new Object[]{1000,1000000},null);
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            //bindingResult.addError(new FieldError("item","quantity",item.getQuantity(),false,new String[]{"max.item.quantity"},new Object[]{9999},null));
            bindingResult.rejectValue("quantity","max",new Object[]{9999},null);
        }

        // 특정 필드 검증이 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                //bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"},new Object[]{10000,resultPrice},null));
                bindingResult.reject("totalPriceMin",new Object[]{10000,resultPrice},null);
            }
        }

        // 검증 실패하면 결과 데이터를 사용자에게 그대로 보내주워야 한다.
        if(bindingResult.hasErrors()) {
            log.info("errors={}",bindingResult);
            return "/validation/v2/addForm";
        }

        // 여기서부터는 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }
}