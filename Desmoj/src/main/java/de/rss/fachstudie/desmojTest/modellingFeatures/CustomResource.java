//package de.rss.fachstudie.desmojTest.modellingFeatures;
//
//import desmoj.core.simulator.Model;
//import desmoj.core.simulator.ModelComponent;
//
//public class CustomResource extends ModelComponent {
//
//    private CustomRes res;
//    private static int resourceNum = 0;
//    private int id;
//    private boolean outOfOrder;
//
//    public CustomResource(Model owner, String name, CustomRes res, boolean showInTrace) {
//        super(owner, name, showInTrace);
//
//        this.id = resourceNum++;
//        rename(name + " resource No. " + id);
//        this.res = res;
//        this.outOfOrder = false;
//    }
//
//    public CustomRes getRes() {
//        return res;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public boolean isOutOfOrder() {
//        return outOfOrder;
//    }
//
//    public void setOutOfOrder(boolean outOfOrder) {
//        this.outOfOrder = outOfOrder;
//    }
//}
