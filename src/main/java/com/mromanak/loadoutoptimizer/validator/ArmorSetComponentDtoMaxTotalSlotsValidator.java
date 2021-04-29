package com.mromanak.loadoutoptimizer.validator;

import com.mromanak.loadoutoptimizer.model.dto.ArmorSetComponentDto;

public class ArmorSetComponentDtoMaxTotalSlotsValidator extends MaxTotalSlotsValidator<ArmorSetComponentDto> {

    @Override
    protected int extractLevel1Slots(ArmorSetComponentDto armorSetComponentDto) {
        return armorSetComponentDto.getLevel1Slots();
    }

    @Override
    protected int extractLevel2Slots(ArmorSetComponentDto armorSetComponentDto) {
        return armorSetComponentDto.getLevel2Slots();
    }

    @Override
    protected int extractLevel3Slots(ArmorSetComponentDto armorSetComponentDto) {
        return armorSetComponentDto.getLevel3Slots();
    }

    @Override
    protected int extractLevel4Slots(ArmorSetComponentDto armorSetComponentDto) {
        return armorSetComponentDto.getLevel4Slots();
    }
}
