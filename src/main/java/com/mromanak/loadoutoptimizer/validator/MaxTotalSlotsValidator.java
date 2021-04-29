package com.mromanak.loadoutoptimizer.validator;

import com.mromanak.loadoutoptimizer.annotations.MaxTotalSlots;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public abstract class MaxTotalSlotsValidator<T> implements ConstraintValidator<MaxTotalSlots, T> {

    private int maxTotalSlots;

    @Override
    public void initialize(MaxTotalSlots constraintAnnotation) {
        maxTotalSlots = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(T t, ConstraintValidatorContext constraintValidatorContext) {
        if (t == null) {
            return true;
        }
        return (extractLevel1Slots(t) + extractLevel2Slots(t) + extractLevel3Slots(t) + extractLevel4Slots(t))
            <= maxTotalSlots;
    }

    protected abstract int extractLevel1Slots(T t);

    protected abstract int extractLevel2Slots(T t);

    protected abstract int extractLevel3Slots(T t);

    protected abstract int extractLevel4Slots(T t);

    protected int nullToZero(Integer integer) {
        return integer == null ? 0 : integer;
    }
}
