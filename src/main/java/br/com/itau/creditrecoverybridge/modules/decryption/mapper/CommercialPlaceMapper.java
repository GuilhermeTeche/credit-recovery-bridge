package br.com.itau.creditrecoverybridge.modules.decryption.mapper;

import br.com.itau.creditrecoverybridge.modules.decryption.model.CommercialModel;
import br.com.itau.creditrecoverybridge.modules.decryption.model.CommercialPlaceDataModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface CommercialPlaceMapper {

    @Mapping(target = "expirationDate", expression = "java( convertToDate(commercialPlaceDataModel.getExpirationYear(), commercialPlaceDataModel.getExpirationMonth()) )")
    @Mapping(target = "currencyCode", constant = "986")
    @Mapping(target = "dateTime", expression = "java( this.formatCurrentDate() )")
    @Mapping(target = "securityCode", expression = "java( formatSecurityCode(commercialPlaceDataModel) )")
    CommercialModel commercialPlaceDataToCommercialModel(CommercialPlaceDataModel commercialPlaceDataModel);

    default String formatSecurityCode(CommercialPlaceDataModel securityCode) {
        String valueSecurityCode = securityCode.getSecurityCode();
        if (securityCode.getSecurityCode().equals("00")) {
            valueSecurityCode = "";
        }
        return valueSecurityCode;
    }

    default String formatCurrentDate() {
        return DateTimeFormatter.ofPattern("yyMMddHHmmss").format(ZonedDateTime.now());
    }

    default String convertToDate(Integer year, Integer month) {
        return MessageFormat.format("{0,number,00}{1,number,00}", year, month);
    }
}
