package com.mromanak.loadoutoptimizer.controller;

import com.mromanak.loadoutoptimizer.model.Loadout;
import com.mromanak.loadoutoptimizer.model.api.ExLoadoutRequest;
import com.mromanak.loadoutoptimizer.model.api.LoadoutRequest;
import com.mromanak.loadoutoptimizer.model.api.LoadoutResponse;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import com.mromanak.loadoutoptimizer.service.LoadoutOptimizerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@RestController
@RequestMapping("/loadout")
@Api(value = "/loadout", description = "Operations for obtaining optimized loadouts", consumes = "application/json",
    produces = "application/json")
public class LoadoutController {

    private final LoadoutOptimizerService loadoutOptimizerService;

    public LoadoutController(LoadoutOptimizerService loadoutOptimizerService) {
        this.loadoutOptimizerService = loadoutOptimizerService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "Returns a list of optimized loadouts that best match pre-made criteria.",
        notes = "Returns a list of optimized loadouts that best match a pre-made request for loadouts that: have 5 " +
            "Earplugs, 3 Windproof, and 3 Tremor Resistance; have as many decoration slots as possible; and use as " +
            "few pieces of armor as possible.", produces = "application/json")
    @Transactional
    public ResponseEntity<List<LoadoutResponse>> getSampleOptimizedLoadouts() {
        LoadoutRequest request = loadoutOptimizerService.getSampleRequest();
        List<LoadoutResponse> loadouts = loadoutOptimizerService.optimize(request).
            stream().
            map((loadout -> toDisplayLoadout(loadout, request.getCompositeScoringFunction().keyFor(loadout)))).
            collect(toList());
        return ResponseEntity.ok(loadouts);
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    @ApiOperation(value = "Returns loadouts that best match the given criteria.", consumes = "application/json",
        produces = "application/json")
    @Transactional
    public ResponseEntity<List<LoadoutResponse>> findOptimizedLoadouts(
        @ApiParam(value = "A set of criteria describing the desired loadouts") @RequestBody LoadoutRequest request)
    {
        List<LoadoutResponse> loadouts = loadoutOptimizerService.optimize(request).
            stream().
            map((loadout -> toDisplayLoadout(loadout, request.getCompositeScoringFunction().keyFor(loadout)))).
            collect(toList());
        return ResponseEntity.ok(loadouts);
    }

    private LoadoutResponse toDisplayLoadout(Loadout loadout, String key) {
        LoadoutResponse loadoutResponse = new LoadoutResponse();
        Map<ArmorType, String> armor = new TreeMap<>(loadout.getArmorPieces().
            entrySet().
            stream().
            collect(toMap(Map.Entry::getKey, e -> e.getValue().getName())));
        loadoutResponse.setArmor(armor);
        loadoutResponse.setSkills(loadout.getSkills());
        loadoutResponse.setLevel1Slots(loadout.getLevel1Slots());
        loadoutResponse.setLevel2Slots(loadout.getLevel2Slots());
        loadoutResponse.setLevel3Slots(loadout.getLevel3Slots());
        loadoutResponse.setLevel4Slots(loadout.getLevel4Slots());
        loadoutResponse.setDefense(loadout.getEffectiveDefense());
        loadoutResponse.setFireResistance(loadout.getEffectiveFireResistance());
        loadoutResponse.setWaterResistance(loadout.getEffectiveWaterResistance());
        loadoutResponse.setThunderResistance(loadout.getEffectiveThunderResistance());
        loadoutResponse.setIceResistance(loadout.getEffectiveIceResistance());
        loadoutResponse.setDragonResistance(loadout.getEffectiveDragonResistance());
        loadoutResponse.setKey(key);
        loadoutResponse.setScore(loadout.getScore());
        return loadoutResponse;
    }
}
