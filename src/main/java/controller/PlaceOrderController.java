package controller;

import entity.cart.Cart;
import entity.cart.CartMedia;
import entity.invoice.Invoice;
import entity.media.Media;
import entity.order.Order;
import entity.order.OrderMedia;
import utils.Utils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class PlaceOrderController extends BaseController {

    private static final Logger LOGGER = Utils.getLogger(PlaceOrderController.class.getName());

    public void checkProductAvailability() throws SQLException {
        Cart.getCart().checkAvailabilityOfProduct();
    }

    public Order createOrder() throws SQLException {
        Order order = new Order();
        List<CartMedia> cartMediaList = Cart.getCart().getListMedia();
        for (CartMedia cartMedia : cartMediaList) {
            OrderMedia orderMedia = new OrderMedia(
                    cartMedia.getMedia(),
                    cartMedia.getQuantity(),
                    cartMedia.getPrice()
            );
            order.getlstOrderMedia().add(orderMedia);
        }
        return order;
    }

    public void placeOrder() throws SQLException {
        Cart.getCart().checkAvailabilityOfProduct();
    }

    public boolean validateContainLetterAndNoEmpty(String name) {
        if (name == null)
            return false;
        if (name.trim().length() == 0)
            return false;
        if (name.matches("^[a-zA-Z ]*$") == false)
            return false;
        return true;
    }

    public Invoice createInvoice(Order order) {
        order.createOrderEntity();
        return new Invoice(order);
    }

    public void processDeliveryInfo(HashMap<String, String> info) throws InterruptedException, IOException {
        validateDeliveryInfo(info);
    }

    private void validateDeliveryInfo(HashMap<String, String> info) {
        if (info == null || info.isEmpty()) {
            throw new IllegalArgumentException("can not be empty");
        }

        String address = info.get("address");
        String phoneNumber = info.get("phoneNumber");

        if (!validateAddress(address)) {
            throw new IllegalArgumentException("Invalid address.");
        }
        if (validatePhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Invalid phone number.");
        }
    }

    public boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != 10 || !phoneNumber.startsWith("0")) {
            return true;
        }

        try {
            Long.parseUnsignedLong(phoneNumber);
        } catch (NumberFormatException e) {
            return true;
        }

        return false;
    }

    private boolean validateAddress(String address) {
        return address != null && !address.trim().isEmpty() && address.matches("^[a-zA-Z0-9, ]+$");
    }

    public int calculateShippingFee(int amount) {
        Random rand = new Random();
        return (int) (((rand.nextFloat() * 10) / 100) * amount);
    }

    public Media getProductAvailableForRushOrder(Order order) throws SQLException {
        for (OrderMedia orderMedia : order.getlstOrderMedia()) {
            if (isMediaEligibleForRushOrder(orderMedia.getMedia())) {
                return orderMedia.getMedia();
            }
        }
        return null;
    }

    private boolean isMediaEligibleForRushOrder(Media media) {
        return media != null && Media.getIsSupportedPlaceRushOrder();
    }

    public boolean validateRushOrderAddress(String province, String address) {
        return "Hà Nội".equals(province) && validateAddress(address);
    }
}
