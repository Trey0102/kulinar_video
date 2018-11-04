package com.aristocrat.cooking;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.aristocrat.cooking.vkgroup.Photo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Nursultan on 18.09.2015.
 */
public class SearchMapCreator {

        private Context context;
        private AssetManager assetManager;
        public HashMap <Integer,ArrayList<RecipeObj> >  allRecipeMap;
        private IngredientWrapper wrapper;

        // < categoryId, < ingredient name, list of recipes> >
        private HashMap< Integer, HashMap<String,ArrayList<SearchItem>>> searchMap;
        private HashMap<String,Boolean> existingmap;
        private HashMap<String, SearchItem> recipeTitleMap;

        private static SearchMapCreator INSTANCE;
        public static SearchMapCreator getInstance(Context context){
                if(INSTANCE==null){
                        Log.e("SMAP","NEW");
                        INSTANCE=new SearchMapCreator(context);
                }
                return INSTANCE;
        }


        private SearchMapCreator(Context context){
                this.context = context;
                this.assetManager=context.getAssets();
                //PREPAREFORSORTING(); // do not call in release apk;
                //BAKEMAPS(); // do not call in release apk;
                //new MorphTask().execute();
                //generate recipesmap;
                BakedMapsWrapper wrapper=loadBakedMaps();
                searchMap=wrapper.searchMap;
                allRecipeMap=wrapper.allRecipeMap;
                existingmap=wrapper.existingmap;
                recipeTitleMap=wrapper.recipeTitleMap;

//                InfoLoader.getInstance((Activity)context).getTransformedRecipes();
                //    int i=1;
                //    Gson gson=new Gson();
                //    for(RecipeObj recipeObj:allRecipeMap.get(11)){
                //           writeNumeratedRecipe(i,recipeObj,gson);
                //            i++;
                //    }

                SQLiteHelper.init(context);
                //createDB();
                readFromDB();
        }

        private void addNew(){
                //stub , adds new recipes to map;
        }

        public ArrayList<RecipeObj> findByIngredients(ArrayList<String> ingredients, int categoryId){
                ArrayList<RecipeObj> recipeObjs=new ArrayList<>();
                HashMap<String,ArrayList<SearchItem>> belmap=new HashMap<>(); // ingredient belonging map.
                ArrayList< ArrayList<SearchItem> > allitems=new ArrayList<>();
                for(int i=0; i<ingredients.size();i++){
                        String item=ingredients.get(i);
                        if(categoryId==0){
                                ArrayList<SearchItem> found=new ArrayList<>();
                                for(int categId:searchMap.keySet()) {
                                        if(categId==11){Log.e("categ11","cgeck");}
                                        HashMap<String,ArrayList<SearchItem> > locmap = searchMap.get(categId);
                                        if(locmap.get(item)!=null) {
                                                if(categId==11){Log.e("categ11","item exists size "+locmap.get(item).size());}
                                                for (SearchItem searchItem : locmap.get(item)) {
                                                        if(categId==11){Log.e("categ11","found");}
                                                        found.add(searchItem);
                                                }
                                        }else {
                                                if(categId==11){Log.e("categ11","item is null");}
                                        }
                                }
                                allitems.add(found);
                        }else {
                                ArrayList<SearchItem> found=new ArrayList<>();
                                HashMap<String,ArrayList<SearchItem> > locmap = searchMap.get(categoryId);
                                if(locmap.get(item)!=null) {
                                        for (SearchItem searchItem : locmap.get(item)) {
                                                found.add(searchItem);
                                        }
                                }
                                allitems.add(found);
                        }

                }

                for(int i=0;i<allitems.size();i++){
                        ArrayList<SearchItem> items=allitems.get(i);
                }

                HashSet result=new HashSet();
                for(int i=0; i<allitems.size();i++){
                        Log.e("itemssize",""+allitems.get(i).size());
                        if(i==0){
                                result.addAll(allitems.get(i));
                        }else {
                                result.retainAll(allitems.get(i));
                        }
                        Log.e("filteredsize"," "+result.size()+" itemssize "+allitems.get(i).size());
                }
                Object[] filtered=result.toArray();
                for(Object o:filtered){
                        SearchItem searchItem=(SearchItem)o;
                        try {
                                recipeObjs.add(allRecipeMap.get(searchItem.categoryId).get(searchItem.filename - 1));
                        }catch(IndexOutOfBoundsException indexex){
                                indexex.printStackTrace();
                        }
                }
                return recipeObjs;
        }

        public ArrayList<RecipeObj> findByName(String text, int categoryId){
                ArrayList<RecipeObj> recipeObjs=new ArrayList<>();
                text=text.toLowerCase();
                for(String recipeName: recipeTitleMap.keySet()){
                        if(recipeName.startsWith(text)){
                                //add recipeobj
                                SearchItem searchItem=recipeTitleMap.get(recipeName);
                                try {
                                        recipeObjs.add(allRecipeMap.get(searchItem.categoryId).get(searchItem.filename - 1));
                                }catch(IndexOutOfBoundsException indexex){
                                        indexex.printStackTrace();
                                }

                        }else if(recipeName.contains(" ")) {
                                String[] separate=recipeName.split(" ");
                                for(String word:separate){
                                        if(word.startsWith(text)){
                                                //add recipeobj
                                                //add recipeobj
                                                SearchItem searchItem=recipeTitleMap.get(recipeName);
                                                try {
                                                        recipeObjs.add(allRecipeMap.get(searchItem.categoryId).get(searchItem.filename - 1));
                                                }catch(IndexOutOfBoundsException indexex){
                                                        indexex.printStackTrace();
                                                }
                                        }
                                }
                        }
                }

                return recipeObjs;
        }


        private void PREPAREFORSORTING() {
                allRecipeMap = new HashMap<>();
                allRecipeMap.put(11, generateRecipesForCategoryId(11));
                BakedMapsWrapper bakedMapsWrapper=new BakedMapsWrapper();
                bakedMapsWrapper.allRecipeMap=allRecipeMap;
                Gson gson=new Gson();
                String serializedbmw=gson.toJson(bakedMapsWrapper);
                Morpher.writeToFile("bakedmapswrapper.txt",serializedbmw,context);
                Log.e("found","size " + allRecipeMap.size());
                Log.e("MAPS", "GENERATED");
        }

        /**
         * нужно только для "запекания" карт поиска. в рабочем апк этот метод не вызывается
         */
        private void BAKEMAPS(){

                allRecipeMap=new HashMap<>();
                for(int i=1; i<12;i++){
                        if(i==10 || i==9){
                                continue;
                        }
                        allRecipeMap.put(i,generateRecipesForCategoryId(i));
                }


                wrapper=loadIngredientWrapper();

                searchMap=new HashMap<>();

                HashMap<String,Ingredient> ingredientsMap=wrapper.getIngredients();
                HashMap<String,Integer> foundmap=new HashMap<>();
                for(int i=1; i<12;i++){
                        if(i==10 || i==9){
                                continue; //we skip non existent 10 category.
                        }
                        Log.e("i",""+i);
                        HashMap<String, ArrayList<SearchItem>> belongingMap = new HashMap<>();
                        for(String item: ingredientsMap.keySet()){
                                ArrayList<SearchItem> searchItems=new ArrayList<>();
                                for(RecipeObj recipeObj:allRecipeMap.get(i)){
                                        boolean exists=false;
                                        if(item.equals("мясо") && isMeat(recipeObj)){
                                                exists=true;
                                        }
                                        if(item.equals("рыба") && isFish(recipeObj)){
                                                exists=true;
                                        }
                                        if(item.equals("птица") && isBird(recipeObj)){
                                                exists=true;
                                        }
                                        if(item.equals("фрукты") && isFruit(recipeObj)){
                                                exists=true;
                                        }
                                        if(item.equals("ягоды") && isBerry(recipeObj)){
                                                exists=true;
                                        }
                                        if(item.equals("овощи") && isVegetable(recipeObj)){
                                                exists=true;
                                        }
                                        if(ingredientsMap.get(item).existsInRecipe(recipeObj.getRecipeText())){
                                                exists=true;
                                        }
                                        if(exists){
                                                if(i==11){
                                                        Log.e("searchitem exists","bug is not here");
                                                }
                                                SearchItem searchItem=new SearchItem();
                                                searchItem.categoryId=i;
                                                searchItem.filename=recipeObj.getFileName();
                                                searchItems.add(searchItem);
                                                existingmap.put(item, true);
                                        }
                                }
                                if(searchItems.size()>0){
                                        foundmap.put(item,0);
                                }
                                belongingMap.put(item, searchItems);
                        }
                        searchMap.put(i, belongingMap);
                }

                BakedMapsWrapper bakedMapsWrapper=new BakedMapsWrapper();
                bakedMapsWrapper.searchMap=searchMap;
                bakedMapsWrapper.allRecipeMap=allRecipeMap;
                bakedMapsWrapper.existingmap= existingmap;
                Gson gson=new Gson();
                String serializedbmw=gson.toJson(bakedMapsWrapper);
                Morpher.writeToFile("bakedmapswrapper.txt",serializedbmw,context);

                Log.e("found","size " + foundmap.size());
                Log.e("MAPS","GENERATED");
        }


        private ArrayList<RecipeObj> generateRecipesForCategoryId ( int categoryId ){
                return InfoLoader.getInstance((Activity) context).loadRecipes(categoryId);
        }


        private BakedMapsWrapper loadBakedMaps(){
                try {

                        InputStream is = assetManager.open("bakedmapswrapper.txt");
                        //BufferedReader reader = new BufferedReader(new InputStreamReader(is, "windows-1251"));
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, InfoLoader.detectEncodingCommon("bakedmapswrapper.txt",assetManager)));
                        String result="";
                        if (is != null) {
                                List<String> buffer = new ArrayList<String>();
                                int nread;
                                char[] buf=new char[4096];
                                while ((nread = reader.read(buf,0,4096)) > 0) {
                                        if(nread<4096){
                                                char[] endbuf=new char[nread];
                                                for(int i=0;i<nread;i++){
                                                        endbuf[i]=buf[i];
                                                }
                                                buffer.add(new String(endbuf));
                                                //add here
                                        }else {
                                                buffer.add(new String(buf));
                                        }
//                                        if(nread<4096){
//                                                char[] endbuf=new char[nread];
//                                                for(int i=0;i<nread;i++){
//                                                        endbuf[i]=buf[i];
//                                                }
//                                                builder.append(endbuf);
//                                        }else {
//                                                builder.append(buf);
//                                        }
                                }
                                result=concatenateStrings(buffer);
                        }
                        // OLD OOME
//                        if (is != null) {
//                                while ((str = reader.readLine()) != null) {
//                                        buf.append(str);
//                                }
//                        }
                        is.close();
                        Gson gson=new Gson();
                        BakedMapsWrapper wrapper=gson.fromJson(result, BakedMapsWrapper.class);
                        return wrapper;
                }catch (Exception ex){
                        ex.printStackTrace();
                }
                return null;
        }

        public static String concatenateStrings(List<String> items)
        {
                if (items == null)
                        return null;
                if (items.size() == 0)
                        return "";
                int expectedSize = 0;
                for (String item: items)
                        expectedSize += item.length();
                StringBuffer result = new StringBuffer(expectedSize);
                for (String item: items)
                        result.append(item);
                return result.toString();
        }

        public ArrayList<String> getUsedIngredients(){
                ArrayList<String> ingredList= new ArrayList<>();
                for(String item : existingmap.keySet()){
                        ingredList.add(item);
                }
                return ingredList;
        }

        private IngredientWrapper loadIngredientWrapper(){
                try {
                        String str = "";
                        StringBuffer buf = new StringBuffer();
                        InputStream is = assetManager.open("ingredientwrapper.txt");
                        //BufferedReader reader = new BufferedReader(new InputStreamReader(is, "windows-1251"));
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, InfoLoader.detectEncodingCommon("ingredientwrapper.txt",assetManager)));
                        if (is != null) {
                                while ((str = reader.readLine()) != null) {
                                        buf.append(str);
                                }
                        }
                        is.close();
                        Gson gson=new Gson();
                        IngredientWrapper wrapper=gson.fromJson(buf.toString(), IngredientWrapper.class);
                        return wrapper;
                }catch (Exception ex){
                        ex.printStackTrace();
                }
                return null;
        }


        private class MorphTask extends AsyncTask<Void , Void , Void>{

                @Override
                protected Void doInBackground(Void... params) {
                        try {
                                HashMap<String,Ingredient> ingredients=new HashMap<>();
                                for(String item:INGREDIENTS){
                                        Morpher morpher=new Morpher(item);
                                        Ingredient ingredient=morpher.getIngredient();
                                        ingredients.put(ingredient.getName(),ingredient);
                                }
                                IngredientWrapper wrapper=new IngredientWrapper();
                                wrapper.setIngredients(ingredients);
                                Gson gson=new Gson();
                                Morpher.writeToFile("ingredientwrapper.txt",gson.toJson(wrapper),context);
                                Log.e("Morpher","finished saving words");
                        }catch (Exception ex){
                                ex.printStackTrace();
                        }
                        return null;
                }

        }

        private boolean isMeat(RecipeObj recipeObj){
                for(int i=12; i<43;i++){
                        if(wrapper.getIngredients().get(INGREDIENTS[i]).existsInRecipe(recipeObj.getRecipeText())){
                                return true;
                        }
                }
                return false;
        }
        private boolean isBird(RecipeObj recipeObj){
                for(int i=2; i<13;i++){
                        if(wrapper.getIngredients().get(INGREDIENTS[i]).existsInRecipe(recipeObj.getRecipeText())){
                                return true;
                        }
                }
                return false;
        }

        private boolean isFish(RecipeObj recipeObj){
                for(int i=142; i<206;i++){
                        if(wrapper.getIngredients().get(INGREDIENTS[i]).existsInRecipe(recipeObj.getRecipeText())){
                                return true;
                        }
                }
                return false;
        }

        private boolean isFruit(RecipeObj recipeObj){
                for(int i=93; i<143;i++){
                        if(wrapper.getIngredients().get(INGREDIENTS[i]).existsInRecipe(recipeObj.getRecipeText())){
                                return true;
                        }
                }
                return false;
        }

        private boolean isBerry(RecipeObj recipeObj){
                for(int i=118; i<143;i++){
                        if(wrapper.getIngredients().get(INGREDIENTS[i]).existsInRecipe(recipeObj.getRecipeText())){
                                return true;
                        }
                }
                return false;
        }

        private boolean isVegetable(RecipeObj recipeObj){
                for(int i=42; i<94;i++){
                        if(wrapper.getIngredients().get(INGREDIENTS[i]).existsInRecipe(recipeObj.getRecipeText())){
                                return true;
                        }
                }
                return false;
        }


        public static String[] INGREDIENTS= {
                "яйцо",
                "перепелиные яйца",
                "гусь",
                "индейка",
                "куриная грудка",
                "куриное филе",
                "курица",
                "куропатки",
                "перепелка",
                "утка",
                "фазан",
                "цыпленок",
                "баранина", //////////////////////////////////////////////МЯСО
                "бекон",
                "ветчина",
                "вырезка",
                "говядина",
                "карбонад",
                "картофель",
                "колбаса",
                "корейка",
                "кролик",
                "курдючное сало",
                "легкое",
                "лосятина",
                "окорок",
                "оленина",
                "печень",
                "потрохи",
                "почки",
                "сало",
                "сардельки",
                "свинина",
                "сервелат",
                "сердце",
                "сосиски",
                "субпродукты",
                "телятина",
                "фарш",
                "шпик",
                "ягненок",
                "язык",
                "артишок", ////////////////////////////////////////////////////////////////ОВОЩИ
                "баклажаны",
                "батат",
                "бобы",
                "болгарский перец",
                "брокколи",
                "брюква",
                "брюссельская капуста",
                "горох",
                "дайкон",
                "кабачки",
                "каперсы",
                "капуста",
                "капуста белокочанная",
                "капуста краснокочанная",
                "капуста цветная",
                "картошка",
                "каштаны",
                "китайская капуста",
                "кольраби",
                "корнишоны",
                "кукуруза",
                "лук порей",
                "лук шалот",
                "луковица",
                "маслины",
                "морковь",
                "овощи",
                "огурцы",
                "оливки",
                "острый перец",
                "пастернак",
                "патиссон",
                "пекинская капуста",
                "помидор",
                "редис",
                "редька",
                "репа",
                "свекла",
                "сельдерей",
                "семечки",
                "сладкий перец",
                "соленые огурцы",
                "спаржа",
                "томат",
                "топинамбур",
                "тыква",
                "цукини",
                "черри",
                "чеснок",
                "шпинат",
                "абрикос", /////////////////////////////////////////ФРУКТЫ
                "авокадо",
                "айва",
                "алыча",
                "ананас",
                "апельсины",
                "банан",
                "грейпфрут",
                "груша",
                "дыня",
                "киви",
                "кокосы",
                "кумкват",
                "лайм",
                "лимон",
                "манго",
                "мандарин",
                "маракуйя",
                "нектарин",
                "папайя",
                "персики",
                "слива",
                "фрукты",
                "хурма",
                "яблоко",
                "клубника",       ////////////////ЯГОДЫ
                "вишня",
                "чернослив",
                "арбуз",
                "брусника",
                "виктория",
                "виноград",
                "ежевика",
                "земляника",
                "клюква",
                "костяника",
                "крыжовник",
                "малина",
                "морошка",
                "облепиха",
                "ранетки",
                "рябина",
                "черешня",
                "смородина",
                "черная смородина",
                "черника",
                "черноплодная рябина",
                "шиповник",
                "голубика",
                "анчоусы", /////////////////РЫБА
                "балык",
                "белуга",
                "вобла",
                "горбуша",
                "ерш",
                "икра",
                "кальмар",
                "камбала",
                "карась",
                "карп",
                "кефаль",
                "кижуч",
                "килька",
                "консервированная рыба",
                "краб",
                "крабовые палочки",
                "красная икра",
                "креветки",
                "лещ",
                "лосось",
                "мидия",
                "минтай",
                "моллюски",
                "морепродукты",
                "морская капуста",
                "морской окунь",
                "морской язык",
                "налим",
                "окунь",
                "омар",
                "осетр",
                "осетрина",
                "осьминог",
                "палтус",
                "пангасиус",
                "пикша",
                "плотва",
                "путасса",
                "раки",
                "сазан",
                "сайда",
                "сайра",
                "салака",
                "сардины",
                "сельдь",
                "семга",
                "скумбрия",
                "сом",
                "ставрида",
                "стерлядь",
                "судак",
                "тилапия",
                "толстолобик",
                "треска",
                "тунец",
                "угрь",
                "форель",
                "хариус",
                "шпроты",
                "щука",
                "язь",
                "басмати",
                "гречка",             //////////////////КРУПЫ
                "крупа",
                "кукурузная крупа",
                "кукурузные хлопья",
                "кускус",
                "манка",
                "овсянка",
                "овсяные хлопья",
                "перловка",
                "пшеница",
                "пшеничная крупа",
                "пшенка",
                "рожки",
                "рис",
                "фасоль",
                "хлопья",
                "чечевица",
                "ячневая крупа",
                "зеленый лук",
                "лук",
                "зелень",
                "зелень петрушки",
                "кинза",
                "колба",
                "крапива",
                "кресс-салат",
                "любисток",
                "мелисса",
                "мята",
                "одуванчик",
                "петрушка",
                "ревень",
                "руккола",
                "салат",
                "терн",
                "укроп",
                "черемша",
                "щавель",
                "эстрагон",
                "адыгейский сыр",
                "ацидофилин",
                "брынза",
                "йогурт",
                "кефир",
                "молоко",
                "молочные продукты",
                "пармезан",
                "плавленый сыр",
                "простокваша",
                "ряженка",
                "сливки",
                "сметана",
                "сухое молоко",
                "сыр",
                "творог",
                "тофу",
                "белые грибы",
                "боровик",
                "вешенки",
                "волнушки",
                "грибы",
                "грузди",
                "дождевики",
                "зеленушки",
                "лисички",
                "маслята",
                "моховики",
                "опята",
                "подберезовик",
                "подосиновик",
                "рыжики",
                "сморчки",
                "сыроежки",
                "трюфели",
                "шампиньоны",
                "шиитаке",
                "арахис",
                "бразильский орех",
                "грецкий орех",
                "кедровый орех",
                "кешью",
                "лесной орех",
                "миндаль",
                "орех",
                "пекан",
                "фисташки",
                "фундук",
                "анис",
                "асафетида",
                "бадьян",
                "базилик",
                "барбарис",
                "белый перец",
                "ваниль",
                "гвоздика",
                "горчица",
                "душистый перец",
                "зира",
                "имбирь",
                "кардамон",
                "карри",
                "кориандр",
                "корица",
                "красный перец",
                "кунжут",
                "куркума",
                "лавровый лист",
                "майоран",
                "можжевельник",
                "мускатный орех",
                "паприка",
                "перец",
                "перец кайенский",
                "перец чили",
                "тимьян",
                "тмин",
                "хрен",
                "чабер",
                "черный перец",
                "чили",
                "шафран",
                "арахисовое масло",
                "кукурузное масло",
                "кунжутное масло",
                "льняное масло",
                "маргарин",
                "оливковое масло",
                "растительное масло",
                "сливочное масло",
                "багет",
                "батон",
                "белый хлеб",
                "вермишель",
                "лаваш",
                "лазанья",
                "макароны",
                "маца",
                "панировочные сухари",
                "равиоли",
                "ракушки",
                "ржаной хлеб",
                "серый хлеб",
                "спагетти",
                "сухари",
                "хлебцы",
                "черный хлеб",
                "бисквитное тесто",
                "заварное тесто",
                "кукурузная мука",
                "мука",
                "овсяная мука",
                "песочное тесто",
                "пшеничная мука",
                "ржаная мука",
                "рисовая мука",
                "тесто пресное",
                "тесто сдобное",
                "изюм",
                "инжир",
                "курага",
                "сухофрукты",
                "урюк",
                "финики",
                "цукаты",
                "гранат",
                "дыня торпеда",
                "ирга",
                "калина",
                "кишмиш",
                "физалис",
                "черемуха",
                "арахисовая паста",
                "бульонные кубики",
                "ванильный сахар",
                "желатин",
                "какао",
                "кокосовая стружка",
                "консервы",
                "майонез",
                "мак",
                "мед",
                "отруби",
                "сахар",
                "соль",
                "соевый соус",
                "соя",
                "томатная паста",
                "тыквенный сок",
                "уксус",
                "халва",
                "чипсы",
                "шоколад",
                "мясо",
                "рыба",
                "специи",
                "птица",
                "ягоды"
        };


        public void writeRecipe(RecipeObj recipeObj,Gson gson){
                Morpher.writeToFile(recipeObj.getFileName()+".txt",gson.toJson(recipeObj),context);
        }

        private void writeNumeratedRecipe(int pos,RecipeObj recipeObj,Gson gson){
                recipeObj.setFileName(pos);
                //  Morpher.writeToFileExternal(pos+".txt",gson.toJson(recipeObj),context);
        }

        private String writeImage(RecipeObj recipeObj,Bitmap bitmapImage){
                ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
                // path to /data/data/yourapp/app_data/imageDir
                File directory = cw.getFilesDir();
                // Create imageDir
                File mypath=new File(directory,recipeObj.getFileName()+".jpg");
                FileOutputStream fos = null;
                try {
                        fos = new FileOutputStream(mypath);
                        // Use the compress method on the BitMap object to write image to the OutputStream
                        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.close();
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return directory.getAbsolutePath();
        }

        public static Bitmap getBitmapFromURL(String link) {
    /*--- this method downloads an Image from the given URL,
     *  then decodes and returns a Bitmap object
     ---*/
                try {
                        URL url = new URL(link);
                        HttpURLConnection connection = (HttpURLConnection) url
                                .openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap myBitmap = BitmapFactory.decodeStream(input);

                        return myBitmap;

                } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("getBmpFromUrl error: ", e.getMessage().toString());
                        return null;
                }
        }


        private void createDB(){
                SQLiteDatabase db=SQLiteHelper.getInstance().getWritableDatabase();
                for(ArrayList<RecipeObj> objList : allRecipeMap.values()){
                        for(RecipeObj obj: objList){
                                // Create a new map of values, where column names are the keys
                                ContentValues values = new ContentValues();
                                values.put(RecipeDBContract.Recipe.COLUMN_NAME_RECIPE_ID,obj.getFileName());
                                values.put(RecipeDBContract.Recipe.COLUMN_NAME_RECIPE_NAME,obj.getRecipeName());
                                values.put(RecipeDBContract.Recipe.COLUMN_NAME_RECIPE_TEXT,obj.getRecipeText());
                                values.put(RecipeDBContract.Recipe.COLUMN_NAME_RECIPE_ATTACHMENTS,new Gson().toJson(obj.getPhotos()));
                                // Insert the new row, returning the primary key value of the new row
                                long newRowId;
                                newRowId = db.insert(
                                        RecipeDBContract.Recipe.TABLE_NAME,
                                        "null",
                                        values);
                        }
                }

                for(ArrayList<RecipeObj> objList: allRecipeMap.values()) {
                        for (RecipeObj obj : objList) {
                                ArrayList<Photo> photos = obj.getPhotos();
                                if (photos == null) {
                                        continue;
                                }
                                for(int x=0;x<photos.size();x++){
                                        String url = photos.get(x).getPhotoUrls().get(photos.get(x).getPhotoUrls().size() - 1);
                                        int width= Integer.parseInt(photos.get(x).getWidth());
                                        int height = Integer.parseInt(photos.get(x).getHeight());
                                        ContentValues values = new ContentValues();
                                        values.put(RecipeDBContract.Attachment.COLUMN_NAME_RECIPE_ID, obj.getFileName());
                                        values.put(RecipeDBContract.Attachment.COLUMN_NAME_POSITION, x);
                                        values.put(RecipeDBContract.Attachment.COLUMN_NAME_URL, url);
                                        values.put(RecipeDBContract.Attachment.COLUMN_NAME_WIDTH, width);
                                        values.put(RecipeDBContract.Attachment.COLUMN_NAME_HEIGHT, height);

                                        long newRowId;
                                        newRowId=db.insert(RecipeDBContract.Attachment.TABLE_NAME,
                                                "null",
                                                values
                                        );
                                }
                        }
                }

                for(int x=0; x < 11; x++){
                        int categoryId= x+1;
                        ContentValues values=new ContentValues();
                        values.put(RecipeDBContract.Category.COLUMN_NAME_CATEGORY_ID, categoryId);
                        values.put(RecipeDBContract.Category.COLUMN_NAME_CATEGORY_NAME,CategoryHelper.getCategoryName(categoryId));
                        long newRowId;
                        newRowId= db.insert(RecipeDBContract.Category.TABLE_NAME,
                                "null",
                                values);
                }

                for (ArrayList<RecipeObj> objList: allRecipeMap.values()){
                        for (RecipeObj obj: objList){
                                ContentValues values =new ContentValues();
                                values.put(RecipeDBContract.Relation.COLUMN_NAME_CATEGORY_ID, CategoryHelper.getCategoryIdByName(obj.getCategoryName()));
                                values.put(RecipeDBContract.Relation.COLUMN_NAME_RECIPE_ID, obj.getFileName());
                                long newRowId;
                                newRowId= db.insert(
                                        RecipeDBContract.Relation.TABLE_NAME,
                                        "null",
                                        values);
                        }
                }
        }


        private ArrayList<RecipeObj> readFromDB(){
                Log.e("START", "START");
                ArrayList<RecipeObj> recipes= new ArrayList<>();

                SQLiteDatabase db= SQLiteHelper.getInstance().getReadableDatabase();

                // Define a projection that specifies which columns from the database
                // you will actually use after this query.
                String[] projection =new String[] {
                        RecipeDBContract.Recipe.COLUMN_NAME_RECIPE_ID,
                        RecipeDBContract.Recipe.COLUMN_NAME_RECIPE_NAME,
                        RecipeDBContract.Recipe.COLUMN_NAME_RECIPE_TEXT,
                        RecipeDBContract.Recipe.COLUMN_NAME_RECIPE_ATTACHMENTS
                };



// How you want the results sorted in the resulting Cursor

//                Cursor cold = db.query(
//                        RecipeDBContract.Recipe.TABLE_NAME,  // The table to query
//                        projection,                               // The columns to return
//                        RecipeDBContract.Recipe.COLUMN_NAME_RECIPE_TEXT+" LIKE ?", // The columns for the WHERE clause
//                        new String[]{"%мясо%"},                            // The values for the WHERE clause
//                        null,                                     // don't group the rows
//                        null,                                     // don't filter by row groups
//                        null                                 // The sort order
//                );

                Cursor c= db.rawQuery("select * from recipes where \t(recipes.recipetext like \"%соль %\" or recipes.recipetext like \"%соли %\" ) and (recipes.recipetext like \"%перец%\" or recipes.recipetext like \"%перца%\" or recipes.recipetext like \"%перцем%\")  and (recipes.recipetext like \"%духов%\")",null);
                Gson gson=new Gson();
                if(c.moveToFirst()){
                        while (c.moveToNext()){
                                RecipeObj recipeObj=new RecipeObj();
                                recipeObj.setRecipeName(c.getString(c.getColumnIndexOrThrow(RecipeDBContract.Recipe.COLUMN_NAME_RECIPE_NAME)));
                                recipeObj.setRecipeText(c.getString(c.getColumnIndexOrThrow(RecipeDBContract.Recipe.COLUMN_NAME_RECIPE_TEXT)));
                                recipeObj.setFileName(c.getInt(c.getColumnIndexOrThrow(RecipeDBContract.Recipe.COLUMN_NAME_RECIPE_ID)));
                                recipeObj.setPhotos((ArrayList<Photo>)(gson.fromJson(c.getString(c.getColumnIndexOrThrow(RecipeDBContract.Recipe.COLUMN_NAME_RECIPE_ATTACHMENTS)), new TypeToken<ArrayList<Photo>>() {}.getType())));
                                recipes.add(recipeObj);
                        }
                }

                c.close();
                Log.e("END","END "+ recipes.size());
                return recipes;
        }



}
