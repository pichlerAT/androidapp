package fry.oldschool.data;

import fry.oldschool.utils.FryFile;

public class TimetableCategory extends OfflineEntry {

    protected int id;

    protected int user_id;

    protected String name;

    public static TimetableCategory create(String name) {
        TimetableCategory cat=new TimetableCategory(0,USER_ID,name);
        ConnectionManager.add(cat);
        return cat;
    }

    protected TimetableCategory(int id,int user_id,String name) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
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
        String resp = getLine("calendar/category/updateTasklists.php","&category_id="+id+"&name="+name);
        return resp.equals("suc");
    }

    public void delete() {
        //App.conMan.add(new Delete(Entry.TYPE_CALENDAR_CATEGORY_DELETE,id));
    }

    @Override
    public void writeTo(FryFile file) {

    }

    protected abstract static class Share extends OfflineEntry {

        protected int contact_id;

        protected int category_id;

        protected Share(int contact_id,int category_id) {
            this.contact_id = contact_id;
            this.category_id = category_id;
        }

        @Override
        public void writeTo(FryFile file) {

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

        }
    }

}
