package com.mromanak.loadoutoptimizer.annotations;


import com.mromanak.loadoutoptimizer.validator.ArmorPieceMaxTotalSlotsValidator;
import com.mromanak.loadoutoptimizer.validator.ArmorPieceDtoMaxTotalSlotsValidator;
import com.mromanak.loadoutoptimizer.validator.ArmorSetComponentDtoMaxTotalSlotsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {
    ArmorPieceMaxTotalSlotsValidator.class,
    ArmorPieceDtoMaxTotalSlotsValidator.class,
    ArmorSetComponentDtoMaxTotalSlotsValidator.class
})
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxTotalSlots {
    int value();
    String message() default "{com.mromanak.loadoutoptimizer.annotations.MaxTotalSlots.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
