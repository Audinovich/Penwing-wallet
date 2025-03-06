package com.Testing.practicasTesteo.service.mappers;

import com.Testing.practicasTesteo.dto.ResponseDTO;
import com.Testing.practicasTesteo.entity.Transactions;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel ="spring")
@Component
public interface TransactionsMapper {


    ResponseDTO createResponseDto(Transactions savedTransaction);
}
