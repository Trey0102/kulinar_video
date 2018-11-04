package com.aristocrat.cooking;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aristocrat.cooking.vkgroup.Photo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Nursultan on 18.09.2015.
 */
public class LocalSearchMapCreator {

        public HashMap <Integer,ArrayList<RecipeObj> >  allRecipeMap;
        private IngredientWrapper wrapper;

        // < categoryId, < ingredient name, list of recipes> >
        private HashMap< Integer, HashMap<String,ArrayList<SearchItem>>> searchMap;
        private HashMap<String,Boolean> existingmap;
        private HashMap<String ,SearchItem> recipeTitleMap;

        private static LocalSearchMapCreator INSTANCE;

        public LocalSearchMapCreator(){
                //PREPAREFORSORTING(); // do not call in release apk;
                //BAKEADVMAP(); // do not call in release apk;
                BAKEMAPS(); // do not call in release apk;
                //new MorphTask().execute();
                //generate recipesmap;
                BakedMapsWrapper wrapper=loadBakedMaps();
                searchMap=wrapper.searchMap;
                allRecipeMap=wrapper.allRecipeMap;
                existingmap=wrapper.existingmap;
                recipeTitleMap=wrapper.recipeTitleMap;

                ArrayList<RecipeObj> result= findByName("плов",0);
                for(RecipeObj recipeObj: result){
                        System.out.println(recipeObj.getRecipeName());
                }

//                InfoLoader.getInstance((Activity)context).getTransformedRecipes();
                //    int i=1;
                //    Gson gson=new Gson();
                //    for(RecipeObj recipeObj:allRecipeMap.get(11)){
                //           writeNumeratedRecipe(i,recipeObj,gson);
                //            i++;
                //    }

        }

        private void addNew(){
                //stub , adds new recipes to map;
        }

        public ArrayList<RecipeObj> findByIngredient(ArrayList<String> ingredients, int categoryId){
                ArrayList<RecipeObj> recipeObjs=new ArrayList<>();
                HashMap<String,ArrayList<SearchItem>> belmap=new HashMap<>(); // ingredient belonging map.
                ArrayList< ArrayList<SearchItem> > allitems=new ArrayList<>();
                for(int i=0; i<ingredients.size();i++){
                        String item=ingredients.get(i);
                        if(categoryId==0){
                                ArrayList<SearchItem> found=new ArrayList<>();
                                for(int categId:searchMap.keySet()) {
                                        HashMap<String,ArrayList<SearchItem> > locmap = searchMap.get(categId);
                                        if(locmap.get(item)!=null) {
                                                for (SearchItem searchItem : locmap.get(item)) {
                                                        found.add(searchItem);
                                                }
                                        }else {

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
                        System.out.println("itemssize " +allitems.get(i).size());
                        if(i==0){
                                result.addAll(allitems.get(i));
                        }else {
                                result.retainAll(allitems.get(i));
                        }
                        System.out.println("filteredsize "+result.size()+" itemssize "+allitems.get(i).size());
                }
                Object[] filtered=result.toArray();
                for(Object o:filtered){
                        SearchItem searchItem=(SearchItem)o;
                        recipeObjs.add(allRecipeMap.get(searchItem.categoryId).get(searchItem.filename-1));
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
                                recipeObjs.add(allRecipeMap.get(searchItem.categoryId).get(searchItem.filename - 1));
                        }else if(recipeName.contains(" ")) {
                                String[] separate=recipeName.split(" ");
                                for(String word:separate){
                                        if(word.startsWith(text)){
                                                //add recipeobj
                                                //add recipeobj
                                                SearchItem searchItem=recipeTitleMap.get(recipeName);
                                                recipeObjs.add(allRecipeMap.get(searchItem.categoryId).get(searchItem.filename - 1));
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
                writeToFile("bakedmapswrapper.txt",serializedbmw);
                System.out.println("found size " + allRecipeMap.size());
                System.out.println("MAPS GENERATED");
        }

        /**
         * нужно только для "запекания" карт поиска. в рабочем апк этот метод не вызывается
         */
        private void BAKEMAPS(){

                allRecipeMap=new HashMap<>();
                for(int i=1; i<12;i++){
                        if( i==9 || i==11){
                                continue;
                        }
                        allRecipeMap.put(i,generateRecipesForCategoryId(i));
                }

                wrapper=loadIngredientWrapper();
                searchMap=new HashMap<>();
                existingmap=new HashMap<>();
                recipeTitleMap=new HashMap<>();

                HashMap<String,Ingredient> ingredientsMap=wrapper.getIngredients();
                HashMap<String,Integer> foundmap=new HashMap<>();
                for(int i=1; i<12;i++){
                        if( i==9 || i==11){
                                continue; //we skip non existent 10 category.
                        }
                        System.out.println("i "+i);
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
                                        SearchItem searchItem=new SearchItem();
                                        searchItem.categoryId=i;
                                        searchItem.filename=recipeObj.getFileName();
                                        if(exists){
                                                searchItems.add(searchItem);
                                                existingmap.put(item,true);
                                        }
                                        recipeTitleMap.put(recipeObj.getRecipeName().toLowerCase(),searchItem);
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
                bakedMapsWrapper.existingmap = existingmap;
                bakedMapsWrapper.recipeTitleMap=recipeTitleMap;
                Gson gson=new Gson();
                String serializedbmw=gson.toJson(bakedMapsWrapper);
                //Morpher.writeToFile("bakedmapswrapper.txt",serializedbmw,context);
                writeToFile("bakedmapswrapper.txt", serializedbmw);
        }

        private void BAKEADVMAP(){

                allRecipeMap=new HashMap<>();
                for(int i=1; i<12;i++){
                        if( i==9 ){
                                continue;
                        }
                        allRecipeMap.put(i,generateRecipesForCategoryId(i));
                }

                wrapper=loadIngredientWrapper();
                searchMap=new HashMap<>();
                existingmap=new HashMap<>();
                recipeTitleMap=new HashMap<>();

                HashMap<String,Ingredient> ingredientsMap=wrapper.getIngredients();
                HashMap<String,Integer> foundmap=new HashMap<>();
                for(int i=1; i<12;i++){
                        if( i==9 ){
                                continue; //we skip non existent 10 category.
                        }
                        System.out.println("i "+i);
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
                                        SearchItem searchItem=new SearchItem();
                                        searchItem.categoryId=i;
                                        searchItem.filename=recipeObj.getFileName();
                                        if(exists){
                                                searchItems.add(searchItem);
                                                existingmap.put(item,true);
                                        }
                                        recipeTitleMap.put(recipeObj.getRecipeName().toLowerCase(),searchItem);
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
                bakedMapsWrapper.existingmap = existingmap;
                bakedMapsWrapper.recipeTitleMap=recipeTitleMap;
                Gson gson=new Gson();
                String serializedbmw=gson.toJson(bakedMapsWrapper);
                //Morpher.writeToFile("bakedmapswrapper.txt",serializedbmw,context);
                writeToFile("bakedmapswrapper.txt",serializedbmw);
        }

        public void writeToFile(String path,String data){
                try {
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(new File(path)));
                        outputStreamWriter.write(data);
                        outputStreamWriter.close();
                }catch (Exception e){
                        e.printStackTrace();
                }

        }


        private ArrayList<RecipeObj> generateRecipesForCategoryId ( int categoryId ){
                System.out.println("generate recipes for "+ categoryId+" "+CategoryHelper.getCategoryName(categoryId));
                return generateFromSerialized(CategoryHelper.getCategoryName(categoryId));
        }

        private ArrayList<RecipeObj> generateFromSerialized(String categoryName) {
                ArrayList<RecipeObj> recipeObjs = new ArrayList<RecipeObj>();
                boolean finished = false;
                int iterator = 0;
                System.out.println(categoryName);
                File folder=new File(categoryName);
                File[] listOfFiles = folder.listFiles();
                int txtcount=0;
                for (int i = 0; i < listOfFiles.length; i++) {
                        File file = listOfFiles[i];
                        if (file.isFile() && file.getName().endsWith(".txt")) {
                                txtcount++;
                        }
                }
                if(categoryName=="adv"){
                        System.out.println("txtcount for category 11: "+txtcount);
                }
                System.out.println("txtcount = " + txtcount);
                while (iterator<txtcount) {
                        try {
                                System.out.println("iterator " +iterator);
                                File file=new File(folder,String.valueOf(iterator + 1) + ".txt");
                                if (file.exists()) {
                                        String str = "";
                                        StringBuffer buf = new StringBuffer();
                                        InputStream is=new FileInputStream(file);
                                        String encoding= detectEncoding(file);
                                        if(encoding=="IBM855"){
                                                System.out.println("wrong encoding in " +(iterator+1)+".txt"+ " in category "+categoryName);
                                                iterator++;
                                                continue;
                                        }
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, encoding));
                                        if (is != null) {
                                                while ((str = reader.readLine()) != null) {
                                                        buf.append(str);
                                                }
                                        }
                                        is.close();
                                        Gson gson=new Gson();
                                        RecipeObj recipeObj=gson.fromJson(buf.toString(), RecipeObj.class);
                                        String recipeName= recipeObj.getRecipeName();

                                        if(!recipeName.substring(0,1).matches("\\p{L}") || !recipeName.substring(0,1).matches("\\p{Lu}")){
                                                if(recipeName.startsWith("\"") || recipeName.startsWith("«")){

                                                }else {
                                                        System.out.println(recipeName);
                                                        recipeName = recipeName.substring(1, recipeName.length());
                                                        recipeName = recipeName.trim();
                                                        System.out.println(recipeName);
                                                }
                                        }

                                        if(
                                                recipeName.startsWith("\uFEFF")
                                                ||recipeName.startsWith("?")
                                                || recipeName.startsWith("!")
                                                || recipeName.startsWith("@")
                                                || recipeName.startsWith("*")){
                                                System.out.println(recipeName);
                                                recipeName=recipeName.substring(1,recipeName.length());
                                                recipeName=recipeName.trim();
                                                System.out.println(recipeName);
                                        }
                                        recipeObj.setRecipeName(recipeName);
                                        recipeObj.setFileName(iterator + 1);
                                        recipeObj.setCategoryName(categoryName);
                                        recipeObjs.add(recipeObj);
                                        iterator++;
                                }
                        }catch (Exception ex){
                                ex.printStackTrace();
                                iterator++;
                                finished=true;
                        }
                }
                return recipeObjs;
        }

        public static String detectEncoding(File file)  throws IOException {
                byte[] buf = new byte[4096];
                UniversalDetector detector=new UniversalDetector(null);
                InputStream is = new FileInputStream(file);
                int nread;
                while ((nread = is.read(buf)) > 0 && !detector.isDone()) {
                        detector.handleData(buf, 0, nread);
                }
                detector.dataEnd();
                String encoding = detector.getDetectedCharset();
                if(encoding!=null && encoding.equals("MACCYRILLIC")){
                        encoding="WINDOWS-1251";
                }else if(encoding==null) {
                        encoding= "WINDOWS-1251";
                }
                return encoding;
        }


        private BakedMapsWrapper loadBakedMaps(){
                try {
                        String str = "";
                        StringBuffer buf = new StringBuffer();
                        InputStream is = new FileInputStream(new File("bakedmapswrapper.txt"));
                        //BufferedReader reader = new BufferedReader(new InputStreamReader(is, "windows-1251"));
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, detectEncoding(new File("bakedmapswrapper.txt"))));
                        if (is != null) {
                                while ((str = reader.readLine()) != null) {
                                        buf.append(str);
                                }
                        }
                        is.close();
                        Gson gson=new Gson();
                        BakedMapsWrapper wrapper=gson.fromJson(buf.toString(), BakedMapsWrapper.class);
                        return wrapper;
                }catch (Exception ex){
                        ex.printStackTrace();
                }
                return null;
        }

        private IngredientWrapper loadIngredientWrapper(){
                try {
                        String str = "";
                        StringBuffer buf = new StringBuffer();
                        InputStream is = new FileInputStream(new File("ingredientwrapper.txt"));
                        //BufferedReader reader = new BufferedReader(new InputStreamReader(is, "windows-1251"));
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, detectEncoding(new File("ingredientwrapper.txt"))));
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


        /* private class MorphTask extends AsyncTask<Void , Void , Void>{

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
                                 System.out.println("Morpher","finished saving words");
                         }catch (Exception ex){
                                 ex.printStackTrace();
                         }
                         return null;
                 }

         }
 */
        private boolean isMeat(RecipeObj recipeObj){
                for(int i=12; i<43;i++){
                        if(wrapper.getIngredients().get(INGREDIENTS[i]).existsInRecipe(recipeObj.getRecipeText())){
                                return true;
                        }
                }
                return false;
        }
        private boolean isBird(RecipeObj recipeObj){
                for(int i=0; i<13;i++){
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
                //    Morpher.writeToFile(recipeObj.getFileName()+".txt",gson.toJson(recipeObj),context);
        }

        private void writeNumeratedRecipe(int pos,RecipeObj recipeObj,Gson gson){
                recipeObj.setFileName(pos);
                //  Morpher.writeToFileExternal(pos+".txt",gson.toJson(recipeObj),context);
        }

//        private String writeImage(RecipeObj recipeObj,Bitmap bitmapImage){
//                        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
//                        // path to /data/data/yourapp/app_data/imageDir
//                        File directory = cw.getFilesDir();
//                        // Create imageDir
//                        File mypath=new File(directory,recipeObj.getFileName()+".jpg");
//                        FileOutputStream fos = null;
//                        try {
//                                fos = new FileOutputStream(mypath);
//                                // Use the compress method on the BitMap object to write image to the OutputStream
//                                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                                fos.close();
//                        } catch (Exception e) {
//                                e.printStackTrace();
//                        }
//                        return directory.getAbsolutePath();
//        }
//
//        public static Bitmap getBitmapFromURL(String link) {
//    /*--- this method downloads an Image from the given URL,
//     *  then decodes and returns a Bitmap object
//     ---*/
//                try {
//                        URL url = new URL(link);
//                        HttpURLConnection connection = (HttpURLConnection) url
//                                .openConnection();
//                        connection.setDoInput(true);
//                        connection.connect();
//                        InputStream input = connection.getInputStream();
//                        Bitmap myBitmap = BitmapFactory.decodeStream(input);
//
//                        return myBitmap;
//
//                } catch (IOException e) {
//                        e.printStackTrace();
//                        System.out.println("getBmpFromUrl error: ", e.getMessage().toString());
//                        return null;
//                }
//        }



}
