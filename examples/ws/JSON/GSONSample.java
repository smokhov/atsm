package json;

import com.google.gson.Gson;

/**
 * Author:  Eric(Haotao) Lai
 * Date:    2018-01-24
 * E-mail:  haotao.lai@gmail.com
 * Website: http://laihaotao.me
 */


public class GSONSample
{

    public static void main(String[] args)
    {
        new GSONSample().run();
    }

    private void run()
    {

        // from json to Java object
        String strJson = "{  \n" +
                         "   \"name\":\"Cessna1\",\n" +
                         "   \"years\":\"1975\",\n" +
                         "   \"make\":\"Piper1\",\n" +
                         "   \"model\":\"Apache1\",\n" +
                         "   \"engine\":\"single1\"\n" +
                         "}";
        Car oCar = (Car) fromJson(strJson);
        System.out.println(oCar);

        System.out.println("= = = = = = = = = = = = = = = =");

        // from Java object to Java
        Car oAnotherCar = new Car();
        oAnotherCar.setName("Cessna2");
        oAnotherCar.setYears("1999");
        oAnotherCar.setMake("Piper2");
        oAnotherCar.setModel("Apache2");
        oAnotherCar.setEngine("single2");
        String strResultJson = toJson(oAnotherCar);
        System.out.println(strResultJson);
    }

    private Object fromJson(String json)
    {
        Gson gson = new Gson();
        return gson.fromJson(json, Car.class);
    }

    private String toJson(Object obj)
    {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }
}
