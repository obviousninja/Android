package us.in_tune.in_tunex3;

/**
 * Created by Randy on 10/12/2015.
 */
public class NameAndPrice {

    private String service_name;
    private String basePrice; //all prices are Doubles
    private String extra_price;
    private String quantity;
    private String service_option;

    public NameAndPrice(){
        this.service_name =null;
        this.basePrice = null;
        this.extra_price = null;
        this.quantity = null;
        this.service_option = null;
    }
    public NameAndPrice(String serviceName, String basePrice, String extraPrice, String quantity, String serviceOption){
        this.service_name =serviceName;
        this.basePrice = basePrice;
        this.extra_price = extraPrice;
        this.quantity = quantity;
        this.service_option = serviceOption;
    }

    public String getServiceName(){
        return service_name;
    }
    public String getBasePrice(){
        return basePrice;
    }
    public String getExtraPrice(){
        return extra_price;
    }
    public String getQuantity(){
        return quantity;
    }
    public String getServiceOption(){
     return service_option;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NameAndPrice that = (NameAndPrice) o;

        if (service_name != null ? !service_name.equals(that.service_name) : that.service_name != null)
            return false;
        if (basePrice != null ? !basePrice.equals(that.basePrice) : that.basePrice != null)
            return false;
        if (extra_price != null ? !extra_price.equals(that.extra_price) : that.extra_price != null)
            return false;
        if (quantity != null ? !quantity.equals(that.quantity) : that.quantity != null)
            return false;
        return !(service_option != null ? !service_option.equals(that.service_option) : that.service_option != null);

    }

    @Override
    public int hashCode() {
        int result = service_name != null ? service_name.hashCode() : 0;
        result = 31 * result + (basePrice != null ? basePrice.hashCode() : 0);
        result = 31 * result + (extra_price != null ? extra_price.hashCode() : 0);
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        result = 31 * result + (service_option != null ? service_option.hashCode() : 0);
        return result;
    }
}
