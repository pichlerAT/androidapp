package fry.oldschool.utils;

public class TimetableCategory extends Entry {

    protected int id;

    protected int user_id;

    protected String name;

    public static TimetableCategory create(String name) {
        TimetableCategory cat=new TimetableCategory(0,USER_ID,name);
        App.conMan.add(cat);
        return cat;
    }

    protected TimetableCategory(int id,int user_id,String name) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
    }

    @Override
    protected String getConManString() {
        return TYPE_CALENDAR_CATEGORY + "" + id + ";" + user_id + ";" + name;
    }

    @Override
    protected boolean mysql() {
        if(id == 0) {
            String resp = getLine("calendar/category/create.php","&name="+name);
            if(resp.substring(0,3).equals("suc")) {
                id = Integer.parseInt(resp.substring(3));
                return true;
            }
            return false;
        }
        String resp = getLine("calendar/category/update.php","&category_id="+id+"&name="+name);
        return resp.equals("suc");
    }

    public void delete() {
        App.conMan.add(new Delete(id));
    }

    protected static class Delete extends Entry {

        protected int id;

        protected Delete(int id) {
            this.id = id;
        }

        @Override
        protected String getConManString() {
            return TYPE_CALENDAR_CATEGORY_DELETE + "" + id;
        }

        @Override
        protected boolean mysql() {
            String resp = getLine("calendar/category/delete.php","&category_id="+id);
            return resp.equals("suc");
        }
    }

    protected abstract static class Share extends Entry {

        protected int contact_id;

        protected int category_id;

        protected Share(int contact_id,int category_id) {
            this.contact_id = contact_id;
            this.category_id = category_id;
        }

        protected static class Create extends Share {

            protected Create(int contact_id,int category_id) {
                super(contact_id,category_id);
            }

            @Override
            protected boolean mysql() {
                String resp = getLine("calendar/category/share/create.php","&category_id="+contact_id+"&category_id="+category_id);
                return resp.equals("suc");
            }

            @Override
            protected String getConManString() {
                return null;
                //return TYPE_CALENDAR_CATEGORY_SHARE_CREATE + "" + contact_id + ";" + category_id;
            }

        }

        protected static class Delete extends Share {

            protected Delete(int contact_id,int category_id) {
                super(contact_id,category_id);
            }

            @Override
            protected boolean mysql() {
                String resp = getLine("calendar/category/share/delete.php","&category_id="+contact_id+"&category_id="+category_id);
                return resp.equals("suc");
            }

            @Override
            protected String getConManString() {
                return null;
                //return TYPE_CALENDAR_CATEGORY_SHARE_DELETE + "" + contact_id + ";" + category_id;
            }

        }
    }

}
