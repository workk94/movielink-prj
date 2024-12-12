package com.acorn.movielink.data;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class PeopleInsert {

    public int insertPeople(String movie_nm_en    ){



        return  0;
    }

    public String getKMDBMovieDatas(String MovieEnNm) throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp?collection=kmdb_new2&");
        /*URL*/
        urlBuilder.append("&" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + URLEncoder.encode("K631OS1085173586S0M9", "UTF-8"));
        /*Service Key*/
        //urlBuilder.append("&" + URLEncoder.encode("val001","UTF-8") + "=" + URLEncoder.encode("2018", "UTF-8"));
        /*상영년도*/
        //urlBuilder.append("&" + URLEncoder.encode("val002","UTF-8") + "=" + URLEncoder.encode("01", "UTF-8"));
        /*상영 월*/

        urlBuilder.append("&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode("Firefighters", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("releaseDts", "UTF-8") + "=" + URLEncoder.encode("2024", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("releaseDte", "UTF-8") + "=" + URLEncoder.encode("2025", "UTF-8"));

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");


        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        String result = sb.toString();



        return result;
    }

    public ArrayList<KMDBMovieActor> getKMDBMovieActorList(String Data, String MovieEnNm){

        JSONObject jsonObject = new JSONObject(Data);
        JSONArray dataArray = jsonObject.getJSONArray("Data");
        JSONObject firstDataObject = dataArray.getJSONObject(0);
        JSONArray jsonResult = firstDataObject.getJSONArray("Result");
        JSONObject firstResultObject = jsonResult.getJSONObject(0);
        JSONObject jsonStaffs = firstResultObject.getJSONObject("staffs");
        JSONArray jsonStaff = jsonStaffs.getJSONArray("staff");

        ArrayList<KMDBMovieActor> KMDBMovieActorList = new ArrayList<>();

        for(int staffNum = 0; staffNum < jsonStaff.length();staffNum++){
            JSONObject staff = jsonStaff.getJSONObject(staffNum);
            String staffRoleGroup = staff.getString("staffRoleGroup");
            if(staffRoleGroup.equals("출연")){
                String staffNm = staff.getString("staffNm");
                String staffEnNm = staff.getString("staffEnNm");
                String staffRole = staff.getString("staffRole");

                System.out.println(staffNm+staffEnNm+staffRole);
                KMDBMovieActorList.add(new KMDBMovieActor(staffNm,staffEnNm,staffRole));
                if (KMDBMovieActorList.size()>=6){return KMDBMovieActorList;}
            }

        }
        return KMDBMovieActorList;
    }

    public ArrayList<KMDBMoiveStaff> getKMDBMovieDirector(String Data, String MovieEnNm){

        JSONObject jsonObject = new JSONObject(Data);
        JSONArray dataArray = jsonObject.getJSONArray("Data");
        JSONObject firstDataObject = dataArray.getJSONObject(0);
        JSONArray jsonResult = firstDataObject.getJSONArray("Result");
        JSONObject firstResultObject = jsonResult.getJSONObject(0);
        JSONObject jsonStaffs = firstResultObject.getJSONObject("staffs");
        JSONArray jsonStaff = jsonStaffs.getJSONArray("staff");

        ArrayList<KMDBMoiveStaff> KMDBMovieStaffList = new ArrayList<>();

        for(int staffNum = 0; staffNum < jsonStaff.length();staffNum++){
            JSONObject staff = jsonStaff.getJSONObject(staffNum);
            String staffRoleGroup = staff.getString("staffRoleGroup");
            if(staffRoleGroup.equals("감독")){
                String staffNm = staff.getString("staffNm");
                String staffEnNm = staff.getString("staffEnNm");

                System.out.println(staffNm+staffEnNm);
                KMDBMovieStaffList.add(new KMDBMoiveStaff(staffNm,staffEnNm));
                if (KMDBMovieStaffList.size()>=6){return KMDBMovieStaffList;}
            }

        }
        return KMDBMovieStaffList;
    }




    //

    public String getTMDBData(String staffEnNm) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.themoviedb.org/3/search/person?query="+URLEncoder.encode( staffEnNm , "UTF-8")+"&include_adult=false&language=ko-kr&page=1"))
                .header("accept", "application/json")
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIzM2FkNmZiNTM3YzM1YjhlMTZjYzZjYjRjMjkyY2U2MiIsIm5iZiI6MTczMjc3ODIzOC42MjUsInN1YiI6IjY3NDgxOGZlMmE1NjIyYjEwNTcxNzMxNCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.pF-ZScaXMg7drI9hB7zeQGsjSQ5iUx5pVrAHtBx0leo")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        return response.body();
    }

    public String getTMDBProfilePath(String TMDBData){
        JSONObject jsonObject = new JSONObject(TMDBData);
        JSONArray jsonResult = jsonObject.getJSONArray("results");
        JSONObject jsonFirstResult = jsonResult.getJSONObject(0);
        String profile_path = jsonFirstResult.getString("profile_path");
        System.out.println(profile_path);

        return profile_path;
    }







    public static void main(String[] args) throws IOException, InterruptedException {

        PeopleInsert pi = new PeopleInsert();
        String MovieEnNm = "The Firefighters";
        String result = pi.getKMDBMovieDatas(MovieEnNm);
        ArrayList<KMDBMovieActor> list = pi.getKMDBMovieActorList(result,MovieEnNm);
        String TMDBData = pi.getTMDBData(list.get(0).getStaffNm());
        String profile_path= pi.getTMDBProfilePath(TMDBData);


    }
}
