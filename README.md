# Hephaestus-Engine

Hephaestus-Engine is a **Java crafting / processing library** built around 3 main concepts:

* **Materials**: definitions (type + categories) and runtime **instances** (`MaterialInstance` with voxel layout)
* **Factories**: runtime machines that accept inputs, run **process recipes**, and produce outputs
* **Process Recipes**: rules that transform inputs into outputs (with optional time windows and events)

It also provides a **planning module** to compute crafting routes (best / top K / all) from available materials to a target.

Hephaestus-Engine is part of **Olympus-Engine**, a collection of libraries and tools for game development.

---

## Features

* [x] Register **materials** with `@MaterialAnnotation`
* [x] Register **factories** with `@FactoryAnnotation` (id, groups, level)
* [x] Register **recipes** with `@RecipeAnnotation` + factory selectors (ids / groups / min level)
* [x] Create factories at runtime and automatically **attach compatible recipes**
* [x] Flexible inputs/outputs matching: **ID**, **ANY**, **ANY_OF_CATEGORIES**, **ALL_OF_CATEGORIES**
* [x] Optional timed processes using `TimeWindow` + processing phases
* [x] Event-driven processes (`FactoryEvent.Action`, `FactoryEvent.VoxelPress`)
* [x] Factory session selection by heuristics (priority / specificity / input count)
* [x] Craft planning: **BEST_ONLY**, **TOP_K**, **ALL** routes with dedup + budgets

---

## Installation

### Gradle

```gradle
dependencies {
    implementation "com.olympus-engine:hephaestus-engine:1.2.0"
}
```

> Replace the version with the one you publish.

---

## Quick Start

### 1) Initialize Hephaestus

Hephaestus is a singleton. You **must** call `Hephaestus.init()` once.

```java
import fr.olympus.hephaestus.Hephaestus;

public class Main {
    public static void main(String[] args) {
        Hephaestus.init();
    }
}
```

### 2) Auto-register materials / factories / recipes

Hephaestus can scan packages using ClassGraph and register annotated classes.

```java
import fr.olympus.hephaestus.Hephaestus;
import fr.olympus.hephaestus.register.RegisterType;

public class Main {
    public static void main(String[] args) {
        Hephaestus.init();

        Hephaestus.autoRegister(RegisterType.MATERIAL, "com.example.heph.materials");
        Hephaestus.autoRegister(RegisterType.FACTORY, "com.example.heph.factories");
        Hephaestus.autoRegister(RegisterType.RECIPE, "com.example.heph.recipes");

        // or:
        // Hephaestus.autoRegister(RegisterType.ALL, "com.example.heph");
    }
}
```

✅ Requirements for auto-register:

* `@MaterialAnnotation` classes must extend `Material`
* `@FactoryAnnotation` classes must extend `Factory`
* `@RecipeAnnotation` classes must implement `ProcessRecipe`
* All annotated classes must have a **no-arg constructor** (can be `protected`, reflection is used)

---

## Materials

### Material definitions

A `Material` is a definition:

* `MaterialType` (marker interface, typically an enum)
* `MaterialCategory` list (marker interface, typically enum)
* name

```java
import fr.olympus.hephaestus.materials.*;

import java.util.List;

public enum ExampleType implements MaterialType {
    INGOT, ORE
}

public enum ExampleCategory implements MaterialCategory {
    METAL, RAW, REFINED
}

@MaterialAnnotation(id = "example:iron_ingot")
public class IronIngotDef extends Material {
    public IronIngotDef() {
        super(ExampleType.INGOT, List.of(ExampleCategory.METAL, ExampleCategory.REFINED), "Iron Ingot");
    }
}
```

### Material instances (runtime)

A `MaterialInstance` is what you actually insert into factories:

```java
import fr.olympus.hephaestus.materials.LayoutBuilder;
import fr.olympus.hephaestus.materials.MaterialInstance;

public class Example {
    public static MaterialInstance createIronIngotVoxel() {
        byte[][][] voxels = LayoutBuilder.create()
                .setSize(4, 2, 2)
                .isPresent(0, 0, 0)
                .isPresent(1, 0, 0)
                .isPresent(2, 0, 0)
                .isPresent(3, 0, 0)
                .build();

        return new MaterialInstance("example:iron_ingot", voxels);
    }
}
```

`LayoutBuilder` provides flags to manage voxel state:

* `PRESENT` (exists)
* `CAN_CHANGE` (editable)
* `CHANGED` (dirty)

---

## Factories

A `Factory`:

* has `contents` (inputs) and `outputs`
* has a list of attached `recipes`
* automatically chooses the best recipe when it can start
* runs a processing session (time-based and/or event-based)

### Creating a Factory class

```java
import fr.olympus.hephaestus.factory.Factory;
import fr.olympus.hephaestus.factory.FactoryAnnotation;
import fr.olympus.hephaestus.processing.MaterialMatcher;

import java.util.List;

@FactoryAnnotation(
        id = "example:anvil_iron",
        groups = {"example:anvil", "example:metal_work"},
        level = 2
)
public class IronAnvilFactory extends Factory {

    public IronAnvilFactory() {
        super();
    }

    @Override
    public void processFinished(List<MaterialMatcher> outputs) {
        // Called when the current session completes.
        // You can react here: play sound, notify UI, consume energy, etc.
        // The produced MaterialInstances are already in the `outputs` list of the factory.
    }
}
```

### Create a factory instance at runtime

When you create a factory through `HephaestusData`, it:

* sets registry meta (id, groups, level)
* attaches **all recipes compatible** with this factory (selector rules)

```java
import fr.olympus.hephaestus.Hephaestus;
import fr.olympus.hephaestus.factory.Factory;

public class Example {
    public static Factory createAnvil() {
        return Hephaestus.getData().createFactory("example:anvil_iron");
    }
}
```

---

## Recipes (ProcessRecipe)

A `ProcessRecipe` describes how to transform inputs into outputs.

### Recipe selection (factoryIds / groups / min level)

`@RecipeAnnotation` supports:

* `factoryIds`: only these exact factory ids
* `factoryGroups`: any factory having at least one of these groups
* `minFactoryLevel`: required minimum level

If selector sets are empty, it means **no constraint** for that dimension.

### Building inputs/outputs with MaterialMatcher

Matchers support:

* `MaterialMatcher.id("example:iron_ingot")`
* `MaterialMatcher.any()`
* `MaterialMatcher.anyOfCategories(Set.of(...))`
* `MaterialMatcher.allOfCategories(Set.of(...))`

They also support quantities: `id(id, qty)` / `any(qty)` / `... (qty)`.

### Implementing a recipe (easy way)

Extend `DefaultProcessRecipe` and override the behavior you need.

```java
import fr.olympus.hephaestus.processing.*;
import fr.olympus.hephaestus.processing.RecipeAnnotation;

import java.util.List;

@RecipeAnnotation(
        id = "example:recipe/iron_plate",
        factoryGroups = {"example:anvil"},
        minFactoryLevel = 2
)
public class IronPlateRecipe extends DefaultProcessRecipe {

    public IronPlateRecipe() {
        super(
                false, // unordered inputs
                List.of(MaterialMatcher.id("example:iron_ingot", 1)),
                List.of(MaterialMatcher.id("example:iron_plate", 1)),
                new TimeWindow(2.0f, 6.0f) // valid window
        );
    }

    @Override
    public void onTick(ProcessContext ctx, fr.olympus.hephaestus.resources.HephaestusData data, float elapsedSeconds, ProcessingPhase phase) {
        // called while a session is running (only if TimeWindow != null)
    }

    @Override
    public void onEvent(ProcessContext ctx, fr.olympus.hephaestus.resources.HephaestusData data, FactoryEvent event, float elapsedSeconds, ProcessingPhase phase) {
        // handle player actions, voxel presses, etc.
    }

    @Override
    public boolean tryComplete(ProcessContext ctx, fr.olympus.hephaestus.resources.HephaestusData data, float elapsedSeconds, ProcessingPhase phase) {
        // DefaultProcessRecipe completes once elapsed >= minSeconds.
        return super.tryComplete(ctx, data, elapsedSeconds, phase);
    }
}
```

> Important: `AutoRegistrar` will instantiate your recipe and call `recipe.registerMeta(ann.id(), selector)`.

---

## Running a Factory

### Start / insert / update

```java
import fr.olympus.hephaestus.Hephaestus;
import fr.olympus.hephaestus.factory.Factory;
import fr.olympus.hephaestus.materials.MaterialInstance;

public class Example {
    public static void run(Factory factory, MaterialInstance input) {
        factory.startFactory();
        factory.insert(input);

        // simulation/game loop
        float dt = 0.016f;
        for (int i = 0; i < 600; i++) {
            factory.update(dt, Hephaestus.getData());
        }

        // collect outputs
        var out = factory.extractAllOutputs();
        System.out.println(out);
    }
}
```

### Push events (interaction-driven)

Events can be injected at any time while operating.

```java
import fr.olympus.hephaestus.Hephaestus;
import fr.olympus.hephaestus.factory.Factory;
import fr.olympus.hephaestus.processing.FactoryEvent;

public class Example {
    public static void hammer(Factory factory) {
        factory.pushEvent(new FactoryEvent.Action("example:hammer", 1.0f), Hephaestus.getData());
        factory.pushEvent(new FactoryEvent.VoxelPress(1, 0, 0, 0, 0.75f), Hephaestus.getData());
    }
}
```

---

## Factory recipe selection (runtime)

When there is no active session, the factory chooses the best recipe that can start using:

1. `priority()` (higher is better)
2. `specificityScore()` (more specific matchers win)
3. `inputCount()` (more inputs win)

This allows you to have:

* generic recipes (fallback)
* specialized recipes (preferred)

---

## Factory Input Policy (optional)

`FactoryInputPolicy` can be used by your gameplay/UI layer to validate whether a material is allowed in a factory.

```java
import fr.olympus.hephaestus.factory.FactoryInputPolicy;
import fr.olympus.hephaestus.materials.*;

import java.util.Set;

public class Example {
    public static FactoryInputPolicy metalOnly() {
        return new FactoryInputPolicy()
                .allowOnlyCategories(Set.of((MaterialCategory) ExampleCategory.METAL))
                .minFactoryLevel(2);
    }
}
```

You typically call `policy.canInsert(materialDef, factoryLevel)` before inserting.

---

## Planning (CraftPlanner)

Hephaestus includes a **backward planner**:

* you give a **target** (`MaterialMatcher`)
* plus a list of **available** materials (`MaterialMatcher`)
* it searches recipes that can produce the target
* recursively plans inputs
* returns best/topK/all plans within limits

### Core planner

```java
import fr.olympus.hephaestus.Hephaestus;
import fr.olympus.hephaestus.planning.CraftPlanner;
import fr.olympus.hephaestus.planning.CraftPlanner.PlanOptions;
import fr.olympus.hephaestus.processing.MaterialMatcher;

import java.util.List;

public class Example {
    public static void plan() {
        var entries = Hephaestus.getData().getProcessRecipeEntriesSnapshot();
        var recipes = entries.stream().map(e -> e.recipe()).toList();

        CraftPlanner planner = new CraftPlanner(recipes);

        MaterialMatcher target = MaterialMatcher.id("example:iron_plate");
        List<MaterialMatcher> available = List.of(MaterialMatcher.id("example:iron_ingot"));

        var opt = PlanOptions.safeDefaults();

        planner.planBest(target, available, opt).ifPresent(plan -> {
            System.out.println("Total cost = " + plan.totalCost());
            System.out.println("Steps = " + plan.steps());
        });
    }
}
```

### PlannerFacade (with category expansion)

If your target is a category matcher, `PlannerFacade` can expand it to concrete ids using the material registry.

```java
import fr.olympus.hephaestus.planning.*;
import fr.olympus.hephaestus.processing.MaterialMatcher;

import java.util.List;

public class Example {
    public static void planWithFacade(CraftPlanner planner) {
        PlannerFacade facade = new PlannerFacade(planner);

        MaterialMatcher target = MaterialMatcher.id("example:iron_plate");
        List<MaterialMatcher> available = List.of(MaterialMatcher.id("example:iron_ingot"));

        var opt = CraftPlanner.PlanOptions.safeDefaults();

        var best = facade.bestOnly(target, available, opt, 128);
        System.out.println(best);
    }
}
```

---

## Manual Registration (without scanning)

If you don’t want ClassGraph scanning, you can register directly via `HephaestusData`:

```java
import fr.olympus.hephaestus.Hephaestus;
import fr.olympus.hephaestus.register.FactoryRegistryEntry;

import java.util.Set;

public class Example {
    public static void manualFactoryRegister() {
        Hephaestus.getData().registerFactory(new FactoryRegistryEntry(
                "example:anvil_iron",
                Set.of("example:anvil"),
                2,
                IronAnvilFactory::new
        ));
    }
}
```

```java
import fr.olympus.hephaestus.Hephaestus;
import fr.olympus.hephaestus.materials.Material;

public class Example {
    public static void manualMaterialRegister(Material def) {
        Hephaestus.getData().registerMaterial("example:iron_ingot", def);
    }
}
```

For recipes, you typically register a `ProcessRecipeRegistryEntry` using a `RecipeSelector` and a recipe instance.

---

## Notes / Gotchas

* `Hephaestus.init()` must be called **once**, otherwise it throws.
* Auto-registration requires **no-arg constructors**.
* `MaterialType` and `MaterialCategory` are marker interfaces and are expected to be **enums** (the code validates `instanceof Enum`).
* `HephaestusData.createFactory(id)` attaches compatible recipes by selector (id/group/level).
* `Factory.update(dt, data)` only ticks time-based recipes (`TimeWindow != null`).

    * For manual recipes (window == null), `tryComplete(...)` can instantly complete (see `DefaultProcessRecipe`).
* `Factory.insert(...)` currently does not enforce input policies by itself; validate externally if needed.
* `MaterialInstance.equals/hashCode` is based only on `materialId` (voxel data is ignored for equality).

---

## Contributing

If you want to contribute, feel free to fork the repository and create a pull request.
You can also report bugs or suggest features by opening an issue.

---

## License

Not yet licensed. The goal is to keep it open source and free to use.
