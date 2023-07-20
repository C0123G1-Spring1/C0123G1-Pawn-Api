package com.example.back_end.controller;

import com.example.back_end.dto.RegisterDTO;
import com.example.back_end.model.ProductType;
import com.example.back_end.service.IProductTypeService;
import com.example.back_end.service.IRegisterPawnService;
import com.sun.istack.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by: ThangND
 * Date created: 13/07/2023
 * Function: register pawn
 *
 * @param: registerDTO
 * @return
 */

@RestController
@RequestMapping("/api/register")
@CrossOrigin("*")
public class RegisterPawnController {
    @Autowired
    private IRegisterPawnService iRegisterPawnService;

    @Autowired
    private IProductTypeService productTypeService;
    @NonNull
    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createRegisterPawn(
            @Validated @RequestBody RegisterDTO registerDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> list = new HashMap<>();

            String[] fieldsToCheck = {"name", "phone", "address", "email", "contentNote"};

            for (String field : fieldsToCheck) {
                @Nullable
                FieldError fieldError = bindingResult.getFieldError(field);
                if (fieldError != null) {
                    list.put(field, fieldError.getDefaultMessage());
                }
            }
            return new ResponseEntity<>(list, HttpStatus.BAD_REQUEST);
        }

        Map<String, String> statusCreate = new HashMap<>();
        statusCreate.put("status", "Đăng Ký Cầm Đồ Thành Công ");
        iRegisterPawnService.createRegisterPawn(registerDTO);
        return new ResponseEntity<>(statusCreate, HttpStatus.OK);
    }


    @GetMapping("/product-type")
    public ResponseEntity<List<ProductType>> getList() {
        if (productTypeService.getAll() == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(productTypeService.getAll(), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public <T> ResponseEntity<T> confirmRegister(@PathVariable("id") Long id) {
        iRegisterPawnService.confirmRegister(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
