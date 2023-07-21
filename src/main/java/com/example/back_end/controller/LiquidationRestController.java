package com.example.back_end.controller;

import com.example.back_end.dto.*;
import com.example.back_end.model.Liquidations;
import com.example.back_end.service.ICustomerService;
import com.example.back_end.service.contracts.IContractsService;
import com.example.back_end.service.customers.ICustomersService;
import com.example.back_end.service.impl.EmailService;
import com.example.back_end.service.liquidations.ILiquidationsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;


/**
 * Created by: KhangPVA
 * Date created: 13/07/2023
 * Function: create liquidation
 * <p>
 * // * @param LiquidaytionDto
 *
 * @return Liquidation
 */

@RestController
@RequestMapping("/api/employee/liquidation")
@CrossOrigin("*")
public class LiquidationRestController {
    @Autowired
    private ILiquidationsService liquidationsService;
    @Autowired
    private ICustomersService customersService;
    @Autowired
    private IContractsService contractsService;
    @Autowired
    private EmailService emailService;


    @PostMapping("")
    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<?> createLiquidation(@Valid @RequestBody LiquidationsDto liquidationsDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
//        Liquidations liquidations = new Liquidations();
//        BeanUtils.copyProperties(liquidationsDto, liquidations);
//        if (bindingResult.hasErrors()) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        liquidationsService.save(liquidations);
        this.contractsService.createLiquidationContract(liquidationsDto);
        String name = liquidationsDto.getCustomers().getName();
        String product = liquidationsDto.getProducts();
        LocalDateTime date= LocalDateTime.now();
        String to = liquidationsDto.getCustomers().getEmail();
        String subject = "Xác nhận mua đồ - PawnShop";
        String body = "Chào " + name + ",\n" +
                "\n" +
                "Chúng tôi gửi mail này để xác nhận rằng bạn vừa thanh toán để mua " + product + " vào ngày " + date +"\n" +
                "\n" +
                "Chúng tôi xin cảm ơn quý khách đã tin tường và sử dụng dịch vụ của chúng tôi.\n" +
                "\n" +
                "Pawn Shop\n" +
                "\n";

        emailService.sendMail(to, subject, body);
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @GetMapping("/customers")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<Page<CustomerListDTO>> getListCustomer(@RequestParam(required = false, defaultValue = "") String name,
                                                               @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC, size = 6) Pageable pageable) {
        Page<CustomerListDTO> CustomerListDTO = customersService.findByNameProduct(name, pageable);
        if (CustomerListDTO.isEmpty() && CustomerListDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(CustomerListDTO, HttpStatus.OK);
    }


    @GetMapping("/contracts")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<Page<IContractDto>> getListProduct(@PageableDefault(size = 6) Pageable pageable, @RequestParam("productName") String productName
            , @RequestParam("productType") String productType, @RequestParam("loans") String loans) {
        Page<IContractDto> contractsDtoPage = contractsService.findAllProduct(pageable, productName, productType, loans);
        if (contractsDtoPage.isEmpty() && contractsDtoPage == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(contractsDtoPage, HttpStatus.OK);
    }



    @GetMapping("/contract/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<IContractDto> getByIdContract(@PathVariable("id") Long id) {
        IContractDto iContractDto = contractsService.findContractById(id);
        if (iContractDto == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(iContractDto, HttpStatus.OK);
    }
}
