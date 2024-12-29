package controller;

import entity.cart.Cart;
import entity.cart.CartMedia;
import entity.media.Media;

import java.util.List;

public class BaseController {

    public CartMedia checkMediaInCart(Media media) {
        if (media == null) {
            throw new IllegalArgumentException("Media cannot be null.");
        }
        return Cart.getCart().checkMediaInCart(media);
    }

    public List<CartMedia> getListCartMedia() {
        return Cart.getCart().getListMedia();
    }
}
