package fr.mrqsdf.ui;

public sealed interface CraftNode permits MaterialNode, FactoryNode {

    String id();      // identifiant unique interne (pas le materialId)
    int x();
    int y();
    int w();
    int h();

    void setPos(int x, int y);
}
