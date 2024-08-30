package com.scalpelred.customstars;

import mcmodutil.Config;
import mcmodutil.ConfigEntryHandle;
import org.slf4j.Logger;

public class CustomStarsClientConfig extends Config {

    public final ConfigEntryHandle<StarModelEnum> StarModel = new ConfigEntryHandle<>("star_model",
            StarModelEnum.class, StarModelEnum.CUBE, """
            Type of star geometry:
            - FLAT: default square star
            - CUBE: cubic star
            - SELECTED: use models specified in \"star_models_array\"
            - DESELECTED: use models not specified in \"star_models_array\"
            - ALL: use all models""");
    public final ConfigEntryHandle<String[]> StarModelsArray = new ConfigEntryHandle<>("star_models_array",
            String[].class, new String[0], "Star models for use if \"star_model\" is set to \"SELECTED\" or \"DESELECTED\"");

    public final ConfigEntryHandle<Integer> StarCount = new ConfigEntryHandle<>("star_count",
            Integer.class, 3000, "The amount of stars");
    public final ConfigEntryHandle<Boolean> RadiusBasedRandomization = new ConfigEntryHandle<>("radius_based_randomization",
            Boolean.class, true, """
            In original Minecraft's star generation algorithm, stars disappears if their non-scaled radius is too big or small.
            No idea what is it for, but seems like some kind of randomization (non-scaled radius is random).""");
    public final ConfigEntryHandle<Float> SkySphereRadius = new ConfigEntryHandle<>("sky_sphere_radius",
            Float.class, 100f, "Radius of sphere containing stars positions");

    public final ConfigEntryHandle<Float> GeneralScaleMin = new ConfigEntryHandle<>("general_scale_min",
            Float.class, 1f);
    public final ConfigEntryHandle<Float> GeneralScaleMax = new ConfigEntryHandle<>("general_scale_max",
            Float.class, 1f);
    public final ConfigEntryHandle<Float> GeneralRotationXMin = new ConfigEntryHandle<>("general_rotation_x_min",
            Float.class, 0f);
    public final ConfigEntryHandle<Float> GeneralRotationXMax = new ConfigEntryHandle<>("general_rotation_x_max",
            Float.class, 0f);
    public final ConfigEntryHandle<Float> GeneralRotationYMin = new ConfigEntryHandle<>("general_rotation_y_min",
            Float.class, 0f);
    public final ConfigEntryHandle<Float> GeneralRotationYMax = new ConfigEntryHandle<>("general_rotation_y_max",
            Float.class, 0f);
    public final ConfigEntryHandle<Float> GeneralRotationZMin = new ConfigEntryHandle<>("general_rotation_z_min",
            Float.class, 0f);
    public final ConfigEntryHandle<Float> GeneralRotationZMax = new ConfigEntryHandle<>("general_rotation_z_max",
            Float.class, 0f);

    public final ConfigEntryHandle<Float> IndividualScaleMin = new ConfigEntryHandle<>("individual_scale_min",
            Float.class, 0.1f);
    public final ConfigEntryHandle<Float> IndividualScaleMax = new ConfigEntryHandle<>("individual_scale_max",
            Float.class, 0.5f);
    public final ConfigEntryHandle<Float> IndividualRotationXMin = new ConfigEntryHandle<>("individual_rotation_x_min",
            Float.class, 0f);
    public final ConfigEntryHandle<Float> IndividualRotationXMax = new ConfigEntryHandle<>("individual_rotation_x_max",
            Float.class, 6.283f);
    public final ConfigEntryHandle<Float> IndividualRotationYMin = new ConfigEntryHandle<>("individual_rotation_y_min",
            Float.class, 0f);
    public final ConfigEntryHandle<Float> IndividualRotationYMax = new ConfigEntryHandle<>("individual_rotation_y_max",
            Float.class, 6.283f);
    public final ConfigEntryHandle<Float> IndividualRotationZMin = new ConfigEntryHandle<>("individual_rotation_z_min",
            Float.class, 0f);
    public final ConfigEntryHandle<Float> IndividualRotationZMax = new ConfigEntryHandle<>("individual_rotation_z_max",
            Float.class, 6.283f);

    public CustomStarsClientConfig(Logger logger) {
        super(logger, false);

        registerEntryHandle(StarModel);
        registerEntryHandle(StarModelsArray);

        registerEntryHandle(StarCount);
        registerEntryHandle(RadiusBasedRandomization);
        registerEntryHandle(SkySphereRadius);

        registerEntryHandle(GeneralScaleMin);
        registerEntryHandle(GeneralScaleMax);
        registerEntryHandle(GeneralRotationXMin);
        registerEntryHandle(GeneralRotationXMax);
        registerEntryHandle(GeneralRotationYMin);
        registerEntryHandle(GeneralRotationYMax);
        registerEntryHandle(GeneralRotationZMin);
        registerEntryHandle(GeneralRotationZMax);
        registerEntryHandle(GeneralRotationXMin);

        registerEntryHandle(IndividualScaleMin);
        registerEntryHandle(IndividualScaleMax);
        registerEntryHandle(IndividualRotationXMax);
        registerEntryHandle(IndividualRotationXMin);
        registerEntryHandle(IndividualRotationYMin);
        registerEntryHandle(IndividualRotationYMax);
        registerEntryHandle(IndividualRotationZMin);
        registerEntryHandle(IndividualRotationZMax);
    }

    public enum StarModelEnum {
        FLAT,
        CUBE,
        SELECTED,
        DESELECTED,
        ALL
    }
}