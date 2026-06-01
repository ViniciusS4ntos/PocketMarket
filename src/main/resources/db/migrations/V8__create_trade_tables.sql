CREATE TABLE trade_offers (
                              id UUID PRIMARY KEY,
                              sender_id UUID NOT NULL,
                              receiver_id UUID NOT NULL,
                              status VARCHAR(30) NOT NULL,
                              created_at TIMESTAMP NOT NULL,

                              CONSTRAINT fk_trade_sender
                                  FOREIGN KEY (sender_id)
                                      REFERENCES users(id),

                              CONSTRAINT fk_trade_receiver
                                  FOREIGN KEY (receiver_id)
                                      REFERENCES users(id)
);

CREATE TABLE trade_offer_items (
                                   id UUID PRIMARY KEY,
                                   trade_offer_id UUID NOT NULL,
                                   user_card_id UUID NOT NULL,
                                   type VARCHAR(20) NOT NULL,

                                   CONSTRAINT fk_trade_item_offer
                                       FOREIGN KEY (trade_offer_id)
                                           REFERENCES trade_offers(id),

                                   CONSTRAINT fk_trade_item_card
                                       FOREIGN KEY (user_card_id)
                                           REFERENCES user_cards(id)
);