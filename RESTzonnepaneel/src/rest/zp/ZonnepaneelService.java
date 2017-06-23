package rest.zp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

@Path("/ZonnepaneelService")
public class ZonnepaneelService {

	//maakt een zonnepaneel aan dmv 2 parameters, code en postcode, hij checkt of de postcode klopt volgens het nederlandse format.
	//checkt daarna of er al een file bestaat, zo niet wordt er een nieuwe file gemaakt en wordt het zonnepaneel daar aan toegevoegd
	//bestaat de file al wel wordt het in het bestaande bestand opgeslagen dmv de methode saveZpList();
	@Path("/create/{code}/{postcode}")
	@GET
	@Produces("application/json")	
	public Response createZP(@PathParam("code") int code,
			@PathParam("postcode") String postcode) throws JSONException {

		List<String> list = null;
		String check = null;
		JSONObject jsonObject = new JSONObject();

		try {
			File file = new File("Zonnepanelen.dat");

			if (postcode.matches("^[1-9][0-9]{3}\\s*(?:[a-zA-Z]{2})?$")) {
				if (!file.exists()) {
					String s = code + "-" + postcode;

					list = new ArrayList<String>();
					list.add(s);
					saveZpList(list);
					jsonObject.put("C Value", code);
					jsonObject.put("PC Value", postcode);

					check = "@Produces(\"application/json\") Output: \n\nOutput: list created \n\n"
							+ jsonObject;

				} else {
					String s = code + "-" + postcode;
					FileInputStream fis = new FileInputStream(file);
					ObjectInputStream ois = new ObjectInputStream(fis);
					list = (List<String>) ois.readObject();

					if (!list.contains(s)) {
						list.add(s);
						FileOutputStream fos;
						fos = new FileOutputStream(file);
						ObjectOutputStream oos = new ObjectOutputStream(fos);
						oos.writeObject(list);
						oos.close();
						jsonObject.put("C Value", code);
						jsonObject.put("PC Value", postcode);

						check = "@Produces(\"application/json\") Output: \n\nOutput: value added \n\n"
								+ jsonObject;
						return Response.status(200).entity(check).build();
					} else {
						check = "@Produces(\"application/json\") Output: \n\nOutput: value already exists\n\n";
					}

					ois.close();

				}
			}

			else {
				check = "@Produces(\"application/json\") Output: \n\nOutput: postcode not valid\n\n";
			}

		} catch (IOException e) {
			check = "@Produces(\"application/json\") Output: \n\nfailed \n\n";
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			check = "@Produces(\"application/json\") Output: \n\nfailed \n\n";
			e.printStackTrace();
		}

		return Response.status(200).entity(check).build();
	}

	@Path("/getlist")
	@GET
	@Produces("application/json")
	public Response getList() throws JSONException, IOException,
			ClassNotFoundException {

		JSONObject jsonObject = new JSONObject();
		List<String> list = new ArrayList<String>();
		List<String> printlist = new ArrayList<String>();
		FileInputStream fis = new FileInputStream("Zonnepanelen.dat");
		ObjectInputStream ois = new ObjectInputStream(fis);
		list = (List<String>) ois.readObject();

		String check = "@Produces(\"application/json\") Output:  \n\n";

		for (int i = 0; i < list.size(); i++) {
			jsonObject.put("Value", list.get(i));
			check += "\n" + jsonObject;
		}

		return Response.status(200).entity(check).build();

	}

	private void saveZpList(List<String> zpList) {
		try {
			File file = new File("Zonnepanelen.dat");
			FileOutputStream fos;
			fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(zpList);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
