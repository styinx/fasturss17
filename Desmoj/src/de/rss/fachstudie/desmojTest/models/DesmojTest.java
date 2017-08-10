package de.rss.fachstudie.desmojTest.models;

import de.rss.fachstudie.desmojTest.entities.*;
import de.rss.fachstudie.desmojTest.events.RequestGeneratorEvent;
import desmoj.core.simulator.*;
import desmoj.core.dist.*;

import java.util.concurrent.TimeUnit;

public class DesmojTest extends Model {

    private static int NUM_ORDER = 1;
    private static int NUM_PROCESSING = 3;
    private static int NUM_SHIPPING = 2;

    private ContDistExponential requestArrivalTime;

    private ContDistUniform orderTime;
    private ContDistUniform processingTime;
    private ContDistUniform shippingTime;

    public Queue<Request> requestQueue;
    public Queue<ProcessingRequest> processingRequestQueue;
    public Queue<ShippingRequest> shippingRequestQueue;

    public Queue<Order> idleOrderQueue;
    public Queue<Processing> idleProcessingQueue;
    public Queue<Shipping> idleShippingQueue;

    public DesmojTest(Model owner, String modelName, boolean showInReport, boolean showInTrace) {
        super(owner, modelName, showInReport, showInTrace);
    }

    /**
     * Required method which returns a description for the model.
     * @return
     */
    @Override
    public String description() {
        return "This model is a test of Desmoj to investigate the suitability of Desmoj for the simulation of microservice architectures.";
    }

    /**
     * Place all events on the internal event list of the simulator which are necessary to start the simulation.
     */
    @Override
    public void doInitialSchedules() {
        RequestGeneratorEvent requestGenerator = new RequestGeneratorEvent(this, "RequestGenerator", true);
        requestGenerator.schedule(new TimeSpan(0));
    }

    /**
     * Initialize static model components like distributions and queues.
     */
    @Override
    public void init() {

        requestArrivalTime = new ContDistExponential(this, "requestArrivalTimeStream", 10, true, false);
        requestArrivalTime.setNonNegative(true);

        orderTime = new ContDistUniform(this,"orderTimeStream", 1.0, 3.5, true, false);
        processingTime = new ContDistUniform(this, "processingTimeStream", 3.0, 5.0, true, false);
        shippingTime = new ContDistUniform(this, "shippingTimeStream", 1.5, 4.0, true, false);

        requestQueue = new Queue<Request>(this, "RequestQueue", true, true);
        processingRequestQueue = new Queue<ProcessingRequest>(this, "ProcessingRequestQueue", true, true);
        shippingRequestQueue = new Queue<ShippingRequest>(this, "ShippingRequestQueue", true, true);

        idleOrderQueue = new Queue<Order>(this, "idleOrderQueue", true, true);
        idleProcessingQueue = new Queue<Processing>(this, "idleProcessingQueue", true, true);
        idleShippingQueue = new Queue<Shipping>(this, "idleShippingQueue", true, true);

        Order order;
        for (int i = 0; i < NUM_ORDER; i++) {
            order = new Order(this, "Order", true);
            idleOrderQueue.insert(order);
        }

        Processing processing;
        for (int i = 0; i < NUM_PROCESSING; i++) {
            processing = new Processing(this, "Processing", true);
            idleProcessingQueue.insert(processing);
        }

        Shipping shipping;
        for (int i = 0; i < NUM_SHIPPING; i++) {
            shipping = new Shipping(this, "Shipping", true);
            idleShippingQueue.insert(shipping);
        }
    }

    public Double getRequestArrivalTime() {
        return requestArrivalTime.sample();
    }

    public Double getOrderTime() {
        return orderTime.sample();
    }

    public Double getProcessingTime() {
        return processingTime.sample();
    }

    public Double getShippingTime() {
        return shippingTime.sample();
    }

    public static void main(String[] args) {
        DesmojTest model = new DesmojTest(null, "Simple microservice model", true, true);
        Experiment exp = new Experiment("DesmojMicroserviceExperiment");

        model.connectToExperiment(exp);

        exp.setShowProgressBarAutoclose(true);
        exp.stop(new TimeInstant(1500, TimeUnit.SECONDS));
        exp.tracePeriod(new TimeInstant(0), new TimeInstant(100, TimeUnit.SECONDS));
        exp.debugPeriod(new TimeInstant(0), new TimeInstant(50, TimeUnit.SECONDS));

        exp.start();

        exp.report();
        exp.finish();
    }
}
