package com.example.usermodule.validation;

import com.example.usermodule.enums.RoleType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

public class RoleTypeValidator implements ConstraintValidator<RoleTypeValidation, String> {
    @Override
    public boolean isValid(String roleType, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isNotBlank(roleType)){
            return EnumUtils.isValidEnum(RoleType.class, roleType);
        }
        return true;
    }
}
