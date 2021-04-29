package com.mromanak.loadoutoptimizer.validator;

import com.mromanak.loadoutoptimizer.model.dto.ArmorPieceDto;

public class ArmorPieceDtoMaxTotalSlotsValidator extends MaxTotalSlotsValidator<ArmorPieceDto> {

    @Override
    protected int extractLevel1Slots(ArmorPieceDto armorPieceDto) {
        return nullToZero(armorPieceDto.getLevel1Slots());
    }

    @Override
    protected int extractLevel2Slots(ArmorPieceDto armorPieceDto) {
        return nullToZero(armorPieceDto.getLevel2Slots());
    }

    @Override
    protected int extractLevel3Slots(ArmorPieceDto armorPieceDto) {
        return nullToZero(armorPieceDto.getLevel3Slots());
    }

    @Override
    protected int extractLevel4Slots(ArmorPieceDto armorPieceDto) {
        return nullToZero(armorPieceDto.getLevel4Slots());
    }
}
