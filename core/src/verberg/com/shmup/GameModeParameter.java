package verberg.com.shmup;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.JsonValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import ecs.Component;

/**
 * Created by Orion on 1/24/2016.
 */
public abstract class GameModeParameter {
    String name;
    String type;

    protected GameModeParameter(JsonValue jv){
        name = jv.getString("name");
        type = jv.getString("type");
    }
    public abstract Actor getView(Skin skin);

    public static class IntParameter extends  GameModeParameter {
        int min, max, increment, cur_val;
        Label vallabel;

        public IntParameter(JsonValue jv){
            super(jv);
            min = jv.getInt("min");
            max = jv.getInt("max");
            cur_val = min;
            increment = jv.getInt("increment");
        }

        public int getValue() {
            return cur_val;
        }

        @Override
        public Actor getView(Skin skin) {
            Table table = new Table();
            Label label = new Label(name, skin);
            table.add(label);
            table.row();

            vallabel = new Label(cur_val + "", skin);

            TextButton decrementBtn = new TextButton("<", skin);

            decrementBtn.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (!(cur_val - increment < min))
                        cur_val = cur_val - increment;
                    vallabel.setText(cur_val + "");
                    return false;
                }
            });
            table.add(decrementBtn);

            table.add(vallabel);

            TextButton incrementBtn = new TextButton(">", skin);

            incrementBtn.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (!(cur_val + increment > max))
                        cur_val = cur_val + increment;
                    vallabel.setText(cur_val + "");
                    return false;
                }
            });
            table.add(incrementBtn);
            return table;
        }
    }

    public static class ComponentParameter extends  GameModeParameter {
        HashMap<String, ArrayList<Component>> comps;

        public ComponentParameter(JsonValue jv){
            super(jv);
            comps = new  HashMap<String, ArrayList<Component>>();
            jv = jv.get("values");
            for(JsonValue comp : jv){
                for(String s : comp.get("applies to").asStringArray()){
                    ArrayList<Component> c;
                    if(comps.containsKey(s)){
                        c = comps.get(s);
                    }else{
                        c = new ArrayList<Component>();
                    }
                    c.add(this.componentFromJV(comp));
                    comps.put(s,c);
                }
            }
            System.out.println("stop here");

        }

        private Component componentFromJV(JsonValue comp){
            String componentName = comp.getString("component");
            String value = comp.getString("value");
            Object instance = null;
            System.out.println(componentName + " val: " + value);
            try {
                Class<?> clazz = Class.forName("ecs.components." + componentName);
                Constructor<?> constructor = clazz.getConstructor(Integer.TYPE);
                instance = constructor.newInstance(Integer.parseInt(value));
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }catch(NoSuchMethodException e){
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return (Component)instance;
        }



        @Override
        public Actor getView(Skin skin) {
            Table table = new Table();
            Label label = new Label(name, skin);
            table.add(label);
            table.row();
            return table;
        }
    }




}
