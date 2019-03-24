# RoomAndSQLiteSample

# Overview

# Manage DB with SQLite

> Việc lưu trữ dữ liệu vào database bên trong thiết bị Android sẽ được thông qua **SQLite Database**, nó dựa trên nền tảng của **SQL Database**. Những thứ bạn cần để thao tác với SQLite là kiến thức cơ bản về SQL, sau đó tất cả những thứ bạn cần đều nằm trong gói **android.database.sqlite**.

* Database sinh ra sẽ được lưu trữ bên trong thư mục app của ứng dụng, đảm bảo việc bảo mật đối với người dùng. Chỉ khi cấp quyền root thì mới có thể xem được thư mục có database.

## Schema, Contract and SQL helper

* Trước khi thao tác với cơ sở dữ liệu SQLite, điều đầu tiên cần phải xác định là lược đồ(schema) của database. Đây chính là cách tổ chức dữ liệu của database, để tạo ra nó bạn có thể thấy hữu ích khi tạo một lớp lưu trữ các thông tin của lược đồ và chỉ rõ ràng bố cục cấu trúc của nó.
* Lớp này sẽ chịu trách nhiệm khai báo tên cho các URI, bảng và cột, chúng ta sử dụng các hằng số giống nhau trên tất cả các lớp khác trong cùng 1 package, điều này dễ dàng cho việc thay đổi sau này.

```
object ContactContract {
    
    object ContactEntry: BaseColumns {
        const val TABLE_NAME = "contact_entry"
        const val COLUMN_NAME = "name"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_ADDRESS = "address"
    }
}
```

* Khi bạn đã xác định được database của mình trông sẽ như thế nào, gồm những trường gì rồi thì tiếp đó bạn nên sử dụng câu lệnh tương đương bên trong **SQL** để có thể tạo ra database dạng bảng, dưới đây là một ví dụ:

```
private const val SQL_CREATE_CONTACT = "CREATE TABLE ${ContactEntry.TABLE_NAME} (" +
            " ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "${ContactEntry.COLUMN_NAME} TEXT, " +
            "${ContactEntry.COLUMN_PHONE} TEXT, " +
            "${ContactEntry.COLUMN_ADDRESS} TEXT " +
            ")"

    private const val SQL_DELETE_CONTACT = "DROP TABLE IF EXISTS ${ContactEntry.TABLE_NAME}"
```

* Tiếp đến bạn có thể tạo một class Helper được implement từ **SQLiteOpenHelper**(đây là class bao gồm các tiện ích của API giúp bạn quản lý database của mình). Khi bạn sử dụng class này tham chiếu đến database, hệ thống sẽ thực hiện các hoạt động có khả năng lâu dài là tạo và cập nhật database chi khi cần chứ không phải trong quá trình khởi động. Bạn chỉ cần gọi **getWritableDatabase() or getReadableDatabase()**, nhưng nên thực hiện trong background thread.

```
class ContactSQLHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_CONTACT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        Log.d(TAG, "onUpgrade: oldVersion:$oldVersion---newVersion:$newVersion")
        db?.execSQL(SQL_DELETE_CONTACT)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onDowngrade: oldVersion:$oldVersion---newVersion:$newVersion")
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        private val TAG = ContactSQLHelper::class.java.simpleName
        // If you change the database schema, you must increment the database version
        const val DATABASE_NAME = "Contact.db"
        const val DATABASE_VERSION = 1
    }
}
```

* Để truy cập vào database, hãy tạo một đối tượng của class helper trên:

```
val contactSQLHelper = ContactSQLHelper(context)
```

## Insert, Read, Update, Delete SQLite Database

### Insert to database

* Thêm dữ liệu vào bảng bên trong SQLite database sử dụng đối tượng của **SQLiteOpenHelper** và cung cấp **writableDatabase** để có thể **insert** được dữ liệu vào:

```
fun insert(contact: Contact): Long {
        val db = contactSQLHelper.writableDatabase
        val values = ContentValues().apply {
            put(ContactContract.ContactEntry.COLUMN_NAME, contact.name)
            put(ContactContract.ContactEntry.COLUMN_PHONE, contact.phone)
            put(ContactContract.ContactEntry.COLUMN_ADDRESS, contact.address)
        }

        return db.insert(ContactContract.ContactEntry.TABLE_NAME, null, values)
    }
```
> Kết quả sẽ trả về row_id nếu thêm thành công, thất bại thì trả về -1.

* Tham số thứ 2 trong phương thức **insert()** sẽ cho biết hành động của framework nếu như **values** trống rỗng(không chứa giá trị nào). Nếu truyền vào là tên của một cột, nó sẽ chèn một hàng và đặt tất cả giá trị đó là null. Còn nếu để là null thì sẽ không có hành động gì.

### Read from database

* Để đọc dữ liệu từ trong database, sử dụng phương thức **query()**, truyền vào tiêu chí lựa chọn của bạn và các cột mong muốn. Kết quả của truy vấn này sẽ trả về trong đối tượng **Cursor**.
* Để đọc ra đối tượng **cursor**, sử dụng một trong các phương thức di chuyển như **moveToNext()**, **moveToFirst()**, ... để đọc được dữ liệu:
* Ban đầu vị trí con trỏ bắt đầu với -1, gọi moveToNext() để di chuyển đặt con trỏ sau đó lấy ra được các kết quả hay không. Tại đây sử dụng phương thức lấy ra dữ liệu đối với từng cursor như là **getLong()**, **getString()**, ... và truyền vào đó chỉ số cột hoặc là tên của cột cần lấy giá trị, cuối cùng là **close()** để giải phóng tài nguyên.

```
fun getContact(name: String): ArrayList<Contact> {
        val db = contactSQLHelper.readableDatabase

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf(
            BaseColumns._ID,
            ContactContract.ContactEntry.COLUMN_NAME,
            ContactContract.ContactEntry.COLUMN_PHONE,
            ContactContract.ContactEntry.COLUMN_ADDRESS
        )

        val selection = "${ContactContract.ContactEntry.COLUMN_NAME} = ?"
        val selectionArgs = arrayOf(name)

        val sortOrder = "${ContactContract.ContactEntry.COLUMN_NAME} DESC"

        val cursor = db.query(
            ContactContract.ContactEntry.TABLE_NAME,    // The table to query
            projection, // The array of columns to return (pass null to get all)
            selection,  // The columns for the WHERE clause
            selectionArgs, // The values for the WHERE clause
            null, // don't group the rows
            null,   // don't filter by row groups
            sortOrder   // The sort order
        )

        val results = arrayListOf<Contact>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val name = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME))
                val phone = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_PHONE))
                val address = getString(getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_ADDRESS))
                val contact = Contact(id, name, phone, address)
                results.add(contact)
            }
        }
        
        // close
        cursor.close()
        db.close()
        
        return results
    }
```

### Delete from database

* Để xóa một hàng từ bảng bên trong database, bạn cần sử dụng phương thức **delete()**. Cơ chế hoạt động giống như các đối số lựa chọn tìm kiếm của phương thức **query()** để có thể tìm được các đối tượng phù hợp.

```
// Return number of row deleted
    fun delete(name: String) : Int{
        val db = contactSQLHelper.writableDatabase

        // Define 'where' part of query.
        val selection = "${ContactContract.ContactEntry.COLUMN_NAME} LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(name)
        val deleteRows = db.delete(ContactContract.ContactEntry.TABLE_NAME, selection, selectionArgs)
        db.close()
        return deleteRows
    }
``` 
> Phương thức này sẽ trả về số lượng row mà database xóa thành công.

## Update to database

* Để update lại các giá trị trong database,sử dụng phương thức **update()**. Tham số sử dụng **contentValue** khi insert và sử dụng điều kiện của xóa.

```
fun update(oldName: String, name: String) : Int{
        val db = contactSQLHelper.writableDatabase

        val values = ContentValues().apply {
            put(ContactContract.ContactEntry.COLUMN_NAME, name)
        }

        val selecstion = "${ContactContract.ContactEntry.COLUMN_NAME} LIKE ?"
        val selectionArgs = arrayOf(oldName)

        val count = db.update(
            ContactContract.ContactEntry.TABLE_NAME,
            values,
            selecstion,
            selectionArgs
        )

        db.close()
        return count
    }
```

> Đến cuối cùng, khi Activity bị hủy đi thì cũng nên close lại **SQLiteOpenHelper**.

# Manage DB with Room

## Why using Room Library?
* Mặc dù các API của SQLite database rất mạnh nhưng lại ở mức độ thấp và yêu cầu nhiều thời gian để implement và sử dụng:
* Không kiểm tra các truy vấn SQL thô trong lúc compile-time. Khi có sự thay đổi của cấu trúc và biểu đồ, sẽ phải viết lại các câu query bị ảnh hưởng. Quá trình này có thể tốn thời gian và dễ gây ra lỗi.
* Cần phải xử lý nhiều đoạn mã giữa truy vấn SQL và các đối tượng dữ liệu.
* Room database có thể kết hợp với LiveData để lắng nghe sự thay đổi của database còn SQLite thì lại không.

Chính vì những lý do trên mà thư viện **Room Persistence** ra đời, sinh ra các giao diện có sẵn để tương tác với database.

## Component of Room

* Cấu trúc của Room bao gồm 3 phần chính sau:

[](https://developer.android.com/jetpack/androidx/releases/room)

* Để thao tác với Room database, thêm chúng vào project tại [đây](https://developer.android.com/topic/libraries/architecture/adding-components)

### Entity
> Class này đại diện cho một bảng trong database. Room sẽ tạo bảng cho mỗi class có annotation là **@Entity**.

#### Annotations

* Có thể định nghĩa một class Entity đơn giản như sau:

```
@Entity
data class User(
    @PrimaryKey var id: Int,
    var firstName: String?,
    var lastName: String?
)
```

* **PrimaryKey**: Chỉ ra khóa chính của thực thể, có thể set cho tự tăng bằng **autoGenerate = true**. Có thể chỉ định nhiều khóa chính trong bảng bằng cách sử dụng `@Entity( primaryKeys = ["first_name", "id"])`

```
@PrimaryKey(autoGenerate = true) val id: Int
```

* **ColumnInfo**: Chỉ ra thông tin của các cột tương ứng trong database.

```
@ColumnInfo(name = "first_name") var firstName: String?
@ColumnInfo(name = "last_name") var lastName: String?
```
* **Ignore** : Sử dụng nếu bạn muốn Room bỏ qua trường nào đó không lưu vào trong database. Nếu như trường hợp kế thừa xảy ra, có thể khai báo trong **@Entity**.

```
open class User {
    var picture: Bitmap? = null
}

@Entity(ignoredColumns = arrayOf("picture"))
data class RemoteUser(
    @PrimaryKey var id: Int,
    var hasVpn: Boolean
) : User()
```

* **Embeded** : các trường lồng nhau có thể tham chiếu trực tiếp khi query.

```
data class Address(
    var city: String?,
    @ColumnInfo(name = "post_code") var postCode: Int
)

@Entity
data class User(
    @PrimaryKey var id: Int,
    var firstName: String?,
    @Embedded var address: Address?
)
```

#### Search support

1. Full-text search

* Nếu muốn ứng dụng truy cập nhanh vào thông tin database thông qua Full-text search(FTS), hãy để các bảng hỗ trợ **FTS3**, **FTS4** bởi SQLite extension.
* Trong trường hợp trong bảng hỗ trợ nội dung có nhiều ngôn ngữ khác nhau, hãy sử dụng **languageId** để chỉ định ngôn ngữ mà bạn chọn để tìm kiếm.

```
// Use `@Fts3` only if your app has strict disk space requirements or if you
// require compatibility with an older SQLite version.
@Fts4(languageId = "lid")
@Entity(tableName = "users")
data class User(
    /* Specifying a primary key for an FTS-table-backed entity is optional, but
       if you include one, it must use this type and column name. */
    @PrimaryKey @ColumnInfo(name = "rowid") var id: Int,
    @ColumnInfo(name = "first_name") var firstName: String?
)
```

2. Index specific columns

* Nếu ứng dụng của bạn có phiên bản mà không hỗ trợ **FTS3** hoặc **FTS4**, bạn vẫn có thể đánh index cho từng cột nhất định trong database để tăng tốc truy vấn. 

```
@Entity(indices = arrayOf(Index(value = ["last_name", "address"])))
data class User(
    @PrimaryKey var id: Int,
    var firstName: String?,
    var address: String?,
    @ColumnInfo(name = "last_name") var lastName: String?,
)
```

* Đôi khi các trường nhất định trong database phải là duy nhất, bạn có thể thực thi việc này với lệnh trong annotation Entity:

```
@Entity(indices = arrayOf(Index(value = ["first_name", "last_name"],
        unique = true)))
```

#### AutoValue
* Giá trị của các lớp trong java được coi là bằng nhau nếu các trường trong đó lần lượt bằng nhau. Ví dụ như sử dụng **equals**, **hashcode**, **toString** để có thể xác định giá trị của object nhưng lại rất dễ bị lỗi.
* Xuất hiện từ version 2.1.0, chúng ta có thể sử dụng **@AutoValue** để cung cấp các giá trị bất biến của đối tượng với ít mã hơn và ít lỗi hơn. 
* Bạn có thể sử dụng **@AutoValue** cho các annotation khác như **@PrimaryKey**, **@ColumnInfo**, **@Embedded**, và **@Relation**. Tuy nhiên triển khai cần thêm annotation **@CopyAnnotations** để Room biết được mỗi lần triển khai cho từng cột cụ thể.

```
@AutoValue
@Entity
public abstract class User {
    // Supported annotations must include `@CopyAnnotations`.
    @CopyAnnotations
    @PrimaryKey
    public abstract long getId();

    public abstract String getFirstName();
    public abstract String getLastName();

    // Room uses this factory method to create User objects.
    public static User create(long id, String firstName, String lastName) {
        return new AutoValue_User(id, firstName, lastName);
    }
}
```

#### Relationship between objects
> Việc quan hệ giữa các bảng trong SQLite rất phổ biến, vì vậy Room cũng hỗ trợ cho chúng ta những công cụ để tạo các khóa ngoại đối với các bảng khác nhau.

* Trong SQL database, rõ ràng là có các quan hệ khác nhau, và chúng có thể tham chiến đến nhau. Nhưng Room lại không cho phép điều này, nó dẫn đến việc đọc dữ liệu chậm và gây hao hụt bộ nhớ khi tải dữ liệu. Chi tiết hơn ở [đây](https://developer.android.com/training/data-storage/room/referencing-data.html#understand-no-object-references)
* Vì vậy Room sẽ hỗ trợ các bảng liên kết bằng cách sử dụng các khóa ngoại để diễn tả mỗi quan hệ của các bảng.
* Ví dụ như để miêu tả mối quan hệ giữa thực thể `Book` với thực thể `User` bằng cách sử dụng annotation **@ForeignKey** như sau:

```
@Entity(foreignKeys = arrayOf(ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("user_id"))
       )
)
data class Book(
    @PrimaryKey var bookId: Int,
    var title: String?,
    @ColumnInfo(name = "user_id") var userId: Int
)

* Sử dụng khóa ngoại rất mạnh mẽ, chúng cho phép bạn chỉ định những gì thực thể được tham chiếu đến cập nhật dữ liệu như thế nào. Chẳng hạn bạn có thể chỉ định xóa hết các **Book** khi mà người dùng bị xóa bằng tùy chọn **onDelete = CASCADE**, tương tự như trong các câu lệnh SQL.

```

### Dao

* Để truy cập dữ liệu sử dụng thư viện Room, bạn cần làm việc với **Data Access Object(DAO)**. Tập hợp các đối tượng Dao này tạo nên thành phần chính của thư viện Room, vì mỗi **Dao** này có quyền truy cập vào database của bạn.
* Bằng cách truy cập vào database thay vì tạo truy vấn hoặc truy vấn trực tiếp, bạn có thể tách ra nhiều thành phần khác nhau của kiến trúc database.
* Room cũng cho phép bạn dễ dàng test được database trên ứng dụng của mình, chi tiết tại [đây](https://developer.android.com/training/data-storage/room/testing-db.html)
* Một Dao là một interface hoặc là lớp abstract. Nếu nó là abstract thì nó có thể có 1 hàm tạo lấy RoomDatabase làm tham số duy nhất.
* Room tạo mỗi triển khai của Dao ngay lúc compile-time. 
* Room không hỗ trợ việc query trên mainThread, vì vậy nếu cần bạn có thể sử dụng phương thức **allowMainThreadQueries()** để chạy trực tiếp trên mainThread.

#### Insert to database

* Để thêm dữ liệu vào database, bạn tạo một phương thức Dao với annotation là **@Insert**, Room sẽ triển khai chèn tất cả các tham số vào 1 lần xử lý.

```
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Insert
    fun insertBothUser(user1: User, user2: User)

    @Insert
    fun insertUserAndContact(user: User, contact: Contact)

}
```

* Nếu phương thức @Insert chỉ có 1 tham số, nó sẽ trả về rowId mới được thêm vào database ở dạng Long. Nếu tham số đầu vào là array hoặc list thì sẽ trả về dạng tương ứng.


#### Update to database

* Phương thức **@Update** sửa đổi một tập hợp các entity, được đưa ra dưới dạng tham số trong database. Chúng sẽ tìm kiếm dựa trên khóa chính của entity để cập nhật.

```
@Update
fun updateUser(vararg user: User)
```

* Mặc dù không cần thiết nhưng phương thức này vẫn trả về số lượng các bản nghi được cập nhật dưới dạng Int.

#### Delete from database

* Phương thức **@Delete** sẽ xóa một tập hợp các entity được đưa ra dưới dạng tham số trong database. Nó cũng tìm kiếm bản nghi phù hợp bằng khóa chính của tham số truyền vào để xóa.

```
@Delete
fun deleteUser(vararg user: User)
```

* Nó cũng trả về các bản nghi được xóa dưới dạng Int.

#### Query form database

* **@Query** là annotation chính trong class Dao, nó cho phép bạn truy vấn đọc và ghi dữ liệu của database.
* Phương thức này được tạo ở lúc compile-time nên nếu có lỗi gì sẽ được báo ngay lúc tạo.
* Room cũng xác minh giá trị trả về của truy vấn sao cho nếu tên truy vấn không khớp với cột cần phản hồi truy vấn, nó sẽ thông báo theo 2 cách :

    * Nó đưa ra cảnh báo nếu chỉ có 1 số tên trường trùng khớp.
    * Nó báo lỗi nếu không có tên trùng khớp.
    
* Một **@Query** đơn giản như sau:

```
@Query("SELECT * FROM user")
fun getAllUser(): List<User>
```
* Thông thường đa số đều sẽ thêm tham số vào để lọc ra đối tượng phù hợp, ví dụ như: 

```
@Query("SELECT * FROM user WHERE age >= :olderAge")
fun getOlderUser(olderAge: Int): List<User>

@Query("SELECT * FROM user WHERE first_name LIKE :search " +
            "OR last_name LIKE :search")
fun findUserWithName(search: String): List<User>
```

* Nếu bạn chỉ muốn trả về một vài cột có trong một bảng thì có thể trực tiếp viết ở trong **@Query**:

```
@Query("SELECT first_name, last_name FROM user")
fun getNameUser(): List<User>
```

* Trường hợp bạn cần phải query tập hợp các đối số, theo ví dụ dưới đây:

```
@Query("SELECT first_name, last_name FROM user WHERE region IN (:regions)")
fun loadUsersFromRegions(regions: List<String>): List<User>
```

* Tạo một truy vấn observable để quan sát sự thay đổi của database, chúng ta sử dụng kết hợp với [Live Data](https://developer.android.com/topic/libraries/architecture/livedata) như sau:
 
```
@Query("SELECT first_name, last_name FROM user WHERE region IN (:regions)")
fun loadUsersFromRegions(regions: List<String>): LiveData<User>
```

* Cũng có thể kết hợp với Rx để quan sát database thay đổi như sau:

```
@Query("SELECT * from user where id = :id LIMIT 1")
fun loadUserById(id: Int): Flowable<User>

// Emits the number of users added to the database.
@Insert
fun insertLargeNumberOfUsers(users: List<User>): Maybe<Int>

// Makes sure that the operation finishes successfully.
@Insert
fun insertLargeNumberOfUsers(varargs users: User): Completable

/* Emits the number of users removed from the database. Always emits at
least one user. */
@Delete
fun deleteAllUsers(users: List<User>): Single<Int>
```

* Nếu ứng dụng của bạn cần phải trả về các row khác nhau để phù hợp với logic, thì việc này Room cũng hỗ trợ:

```
@Query("SELECT * FROM user WHERE age > :minAge LIMIT 5")
fun loadRawUsersOlderThan(minAge: Int): Cursor
```

* Query nhiều bảng khác nhau sử dụng câu lệnh như **JOIN** như trong SQL, như sau:

```
@Dao
interface MyDao {
    @Query(
        "SELECT * FROM book " +
        "INNER JOIN loan ON loan.book_id = book.id " +
        "INNER JOIN user ON user.id = loan.user_id " +
        "WHERE user.name LIKE :userName"
    )
    fun findBooksBorrowedByNameSync(userName: String): List<Book>
}
```

### Database

* Để tạo một database, hãy tạo class abstract kế thừa từ **RoomDatabase** với annotation là **@Database**. Truyền vào annotation này là danh sách các entity, version, exportSchema.

```
@Database(entities = [User::class], exportSchema = true, version = MyRoomDatabase.DATABASE_VERSION)
abstract class MyRoomDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao

    companion object {
        const val DATABASE_NAME = "RoomUserDB"
        const val DATABASE_VERSION = 1

        var INSTANCE: MyRoomDatabase? = null

        fun getAppDataBase(context: Context): MyRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(MyRoomDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        MyRoomDatabase::class.java,
                        DATABASE_NAME
                    ).allowMainThreadQueries()
                    .build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase() {
            INSTANCE = null
        }
    }
}
```

* Việc thêm **allowMainThreadQueries()** thì Room không khuyến nghị vì có thể dẫn đến việc UI bị đứng hình vì load dữ liệu lâu trên Main Thread.