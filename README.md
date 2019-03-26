# RoomAndSQLiteSample

# Overview

* Lưu trữ dữ liệu trong Android sử dụng SQLite để quản lý database, nằm trong package [android.database.sqlite](https://developer.android.com/reference/android/database/sqlite/package-summary.html). Nó sử dụng ngôn ngữ truy vấn tương tự như SQL để viết các lệnh **Insert**, **Update**, **Delete**, **Query** cùng với các câu lệnh tạo bảng như trên SQL.
* Database này được lưu trữ bên trong thư mục của ứng dụng khi người dùng cài đặt, nếu không phải là quền root thì sẽ không xem được.
* SQLite cung cấp một số API để cho người dùng sử dụng để quản lý database của mình.
* Database sinh ra sẽ được lưu trữ bên trong thư mục app của ứng dụng, đảm bảo việc bảo mật đối với người dùng. Chỉ khi cấp quyền root thì mới có thể xem được thư mục có database.
* Document chi tiết hơn ở [đây](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md) 

# Manage DB with SQLite

> Việc lưu trữ dữ liệu vào database bên trong thiết bị Android sẽ được thông qua **SQLite Database**, nó dựa trên nền tảng của **SQL Database**. Những thứ bạn cần để thao tác với SQLite là kiến thức cơ bản về SQL, sau đó tất cả những thứ bạn cần đều nằm trong gói **android.database.sqlite**.

## [Schema, Contract and SQL helper](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#schema-contract-and-sql-helper) 

* Cần xác định lược đồ của các bảng trong database, sau đó viết lệnh SQL để tạo bảng như sau:
* Sử dụng **SQLiteOpenHelper** để thao tác với database, sử dụng hàm **onCreate()** để tạo và **onUpgrade()** để cập nhật Database. Class này cung cấp đối tượng để read và write database như **getWritableDatabase() or getReadableDatabase()** để thao tác.

## Insert, Read, Update, Delete SQLite Database

### [Insert to database](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#insert-to-database)

* Thêm dữ liệu vào bảng bên trong SQLite database sử dụng đối tượng của **SQLiteOpenHelper** và cung cấp **writableDatabase** để có thể **insert** được dữ liệu vào:

### [Read from database](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#read-from-database)

* Để đọc dữ liệu từ trong database, sử dụng phương thức **query()**, truyền vào tiêu chí lựa chọn của bạn và các cột mong muốn. Kết quả của truy vấn này sẽ trả về trong đối tượng **Cursor**.
* Để đọc ra đối tượng **cursor**, sử dụng một trong các phương thức di chuyển như **moveToNext()**, **moveToFirst()**, ... để đọc được dữ liệu:
* Ban đầu vị trí con trỏ bắt đầu với -1, gọi moveToNext() để di chuyển đặt con trỏ sau đó lấy ra được các kết quả hay không. Tại đây sử dụng phương thức lấy ra dữ liệu đối với từng cursor như là **getLong()**, **getString()**, ... và truyền vào đó chỉ số cột hoặc là tên của cột cần lấy giá trị, cuối cùng là **close()** để giải phóng tài nguyên.

### [Delete from database](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#delete-from-database)

* Để xóa một hàng từ bảng bên trong database, bạn cần sử dụng phương thức **delete()**. Cơ chế hoạt động giống như các đối số lựa chọn tìm kiếm của phương thức **query()** để có thể tìm được các đối tượng phù hợp.

## [Update to database](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#update-to-database)

* Để update lại các giá trị trong database,sử dụng phương thức **update()**. Tham số sử dụng **contentValue** khi insert và sử dụng điều kiện của xóa.

## [Migration Change](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#migration-change)

* Việc migration trên API SQLite thực hiện sẽ rất khó khăn và không cẩn thận một chút là đã tự hủy đi database của người dùng trong ứng dụng.
* Việc phải cập nhật database với các phiên bản sau sẽ xảy ra 2 trường hợp sau:

    * Nếu người dùng cài đặt ứng dụng mà chưa có version 1 thì ứng dụng sẽ chạy vào phương thức **DbHelper.onCreate(db)**.
    * Nếu người dùng cập nhật từ phiên bản version 1 lên 2 thì ứng dụng sẽ chạy vào phương thức **DbHelper.onUpgrade(db… )**.
    
* Việc đầu tiên cần làm để cập nhật database lên phiên bản mới nhất là viết lệnh SQL để đưa các cơ sở dữ liệu lên phiên bản mới nhất(đối với các máy chưa có version nào).
* Khi đã tạo được câu lệnh để cập nhật, phải dựa vào database trong hàm **onUpgrade()** để lấy dữ liệu cũ ra và cập nhật vào bảng mới nếu có thêm cột mới:
* Việc cập nhật đối với từng phiên bản sẽ được triển khai thủ công bên trong hàm **onUpgrade()** để đảm bảo người dùng có database mới nhất đối với sự thay đổi version tiếp theo:

# Manage DB with Room

## Why using Room Library?
* Mặc dù các API của SQLite database rất mạnh nhưng lại ở mức độ thấp và yêu cầu nhiều thời gian để implement và sử dụng:
* Không kiểm tra các truy vấn SQL thô trong lúc compile-time. Khi có sự thay đổi của cấu trúc và biểu đồ, sẽ phải viết lại các câu query bị ảnh hưởng. Quá trình này có thể tốn thời gian và dễ gây ra lỗi.
* Cần phải xử lý nhiều đoạn mã giữa truy vấn SQL và các đối tượng dữ liệu.
* Room database có thể kết hợp với LiveData để lắng nghe sự thay đổi của database còn SQLite thì lại không.

Chính vì những lý do trên mà thư viện **Room Persistence** ra đời, sinh ra các giao diện có sẵn để tương tác với database.

## [Component of Room](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#component-of-room)

* Cấu trúc của Room bao gồm 3 phần chính sau:

![](https://developer.android.com/images/training/data-storage/room_architecture.png)
 

* Để thao tác với Room database, thêm chúng vào project tại [đây](https://developer.android.com/topic/libraries/architecture/adding-components)

### [Entity](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#entity)
> Class này đại diện cho một bảng trong database. Room sẽ tạo bảng cho mỗi class có annotation là **@Entity**.

#### [Annotations](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#annotations)

* **@PrimaryKey**: Chỉ ra khóa chính của thực thể, có thể set cho tự tăng bằng **autoGenerate = true**. Có thể chỉ định nhiều khóa chính trong bảng bằng cách sử dụng `@Entity( primaryKeys = ["first_name", "id"])`
* **@ColumnInfo**: Chỉ ra thông tin của các cột tương ứng trong database.
* **@Ignore** : Sử dụng nếu bạn muốn Room bỏ qua trường nào đó không lưu vào trong database. Nếu như trường hợp kế thừa xảy ra, có thể khai báo trong **@Entity**.
* **@Embeded** : các trường lồng nhau có thể tham chiếu trực tiếp khi query.

#### [Search support](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#search-support)

1. Full-text search

* Nếu muốn ứng dụng truy cập nhanh vào thông tin database thông qua Full-text search(FTS), hãy để các bảng hỗ trợ **FTS3**, **FTS4** bởi SQLite extension.
* Trong trường hợp trong bảng hỗ trợ nội dung có nhiều ngôn ngữ khác nhau, hãy sử dụng **languageId** để chỉ định ngôn ngữ mà bạn chọn để tìm kiếm.

2. Index specific columns

* Nếu ứng dụng của bạn có phiên bản mà không hỗ trợ **FTS3** hoặc **FTS4**, bạn vẫn có thể đánh index cho từng cột nhất định trong database để tăng tốc truy vấn. 

#### [AutoValue](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#autovalue)

* Giá trị của các lớp trong java được coi là bằng nhau nếu các trường trong đó lần lượt bằng nhau. Ví dụ như sử dụng **equals**, **hashcode**, **toString** để có thể xác định giá trị của object nhưng lại rất dễ bị lỗi.
* Xuất hiện từ version 2.1.0, chúng ta có thể sử dụng **@AutoValue** để cung cấp các giá trị bất biến của đối tượng với ít mã hơn và ít lỗi hơn. 
* Bạn có thể sử dụng **@AutoValue** cho các annotation khác như **@PrimaryKey**, **@ColumnInfo**, **@Embedded**, và **@Relation**. Tuy nhiên triển khai cần thêm annotation **@CopyAnnotations** để Room biết được mỗi lần triển khai cho từng cột cụ thể.

#### [Relationship between objects](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#relationship-between-objects)

> Việc quan hệ giữa các bảng trong SQLite rất phổ biến, vì vậy Room cũng hỗ trợ cho chúng ta những công cụ để tạo các khóa ngoại đối với các bảng khác nhau.

* Trong SQL database, rõ ràng là có các quan hệ khác nhau, và chúng có thể tham chiến đến nhau. Nhưng Room lại không cho phép điều này, nó dẫn đến việc đọc dữ liệu chậm và gây hao hụt bộ nhớ khi tải dữ liệu. Chi tiết hơn ở [đây](https://developer.android.com/training/data-storage/room/referencing-data.html#understand-no-object-references)
* Vì vậy Room sẽ hỗ trợ các bảng liên kết bằng cách sử dụng các khóa ngoại để diễn tả mỗi quan hệ của các bảng.
* Ví dụ như để miêu tả mối quan hệ giữa thực thể `Book` với thực thể `User` bằng cách sử dụng annotation **@ForeignKey** như sau:
* Sử dụng khóa ngoại rất mạnh mẽ, chúng cho phép bạn chỉ định những gì thực thể được tham chiếu đến cập nhật dữ liệu như thế nào. Chẳng hạn bạn có thể chỉ định xóa hết các **Book** khi mà người dùng bị xóa bằng tùy chọn **onDelete = CASCADE**, tương tự như trong các câu lệnh SQL.

### [Dao](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#dao)

* Để truy cập dữ liệu sử dụng thư viện Room, bạn cần làm việc với **Data Access Object(DAO)**. Tập hợp các đối tượng Dao này tạo nên thành phần chính của thư viện Room, vì mỗi **Dao** này có quyền truy cập vào database của bạn.
* Bằng cách truy cập vào database thay vì tạo truy vấn hoặc truy vấn trực tiếp, bạn có thể tách ra nhiều thành phần khác nhau của kiến trúc database.
* Room cũng cho phép bạn dễ dàng test được database trên ứng dụng của mình, chi tiết tại [đây](https://developer.android.com/training/data-storage/room/testing-db.html)
* Một Dao là một interface hoặc là lớp abstract. Nếu nó là abstract thì nó có thể có 1 hàm tạo lấy RoomDatabase làm tham số duy nhất.
* Room tạo mỗi triển khai của Dao ngay lúc compile-time. 
* Room không hỗ trợ việc query trên mainThread, vì vậy nếu cần bạn có thể sử dụng phương thức **allowMainThreadQueries()** để chạy trực tiếp trên mainThread.

#### [Insert to database](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#insert-to-database-1)

* Để thêm dữ liệu vào database, bạn tạo một phương thức Dao với annotation là **@Insert**, Room sẽ triển khai chèn tất cả các tham số vào 1 lần xử lý cho dù bạn có truyền vào nhiều tham số cùng 1 lúc:
* Nếu phương thức @Insert chỉ có 1 tham số, nó sẽ trả về rowId mới được thêm vào database ở dạng Long. Nếu tham số đầu vào là array hoặc list thì sẽ trả về dạng tương ứng.

#### [Update to database](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#update-to-database-1)

* Phương thức **@Update** sửa đổi một tập hợp các entity, được đưa ra dưới dạng tham số trong database. Chúng sẽ tìm kiếm dựa trên khóa chính của entity để cập nhật.
* Mặc dù không cần thiết nhưng phương thức này vẫn trả về số lượng các bản nghi được cập nhật dưới dạng Int.

#### [Delete from database](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#delete-from-database-1)

* Phương thức **@Delete** sẽ xóa một tập hợp các entity được đưa ra dưới dạng tham số trong database. Nó cũng tìm kiếm bản nghi phù hợp bằng khóa chính của tham số truyền vào để xóa.
* Nó cũng trả về các bản nghi được xóa dưới dạng Int.

#### [Query form database](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#query-form-database)

* **@Query** là annotation chính trong class Dao, nó cho phép bạn truy vấn đọc và ghi dữ liệu của database.
* Phương thức này được tạo ở lúc compile-time nên nếu có lỗi gì sẽ được báo ngay lúc tạo.
* Room cũng xác minh giá trị trả về của truy vấn sao cho nếu tên truy vấn không khớp với cột cần phản hồi truy vấn, nó sẽ thông báo theo 2 cách :

    * Nó đưa ra cảnh báo nếu chỉ có 1 số tên trường trùng khớp.
    * Nó báo lỗi nếu không có tên trùng khớp.
    
* Thông thường đa số đều sẽ thêm tham số vào để lọc ra đối tượng phù hợp, chỉ cần thêm vào tham số của hàm được gọi, ví dụ như: 

```
@Query("SELECT * FROM user WHERE age >= :olderAge")
fun getOlderUser(olderAge: Int): List<User>
```

* Trường hợp bạn cần phải query tập hợp các đối số, cũng như trên truyền vào đầu vào của phương thức là được.
* Tạo một truy vấn observable để quan sát sự thay đổi của database, chúng ta sử dụng kết hợp với [Live Data](https://developer.android.com/topic/libraries/architecture/livedata).
* Cũng có thể kết hợp với Rx để quan sát database thay đổi sử dụng **Flowable**, **Maybe**, **Single**, **Completable** như sau:
* Nếu ứng dụng của bạn cần phải trả về các row khác nhau để phù hợp với logic, thì việc này Room cũng hỗ trợ trả về dạng **Curso**.
* Query nhiều bảng khác nhau sử dụng câu lệnh như **JOIN** như trong SQL để truy vấn từ nhiều bàng. 

### [Database](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#database)

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

## [Migration with Room](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#migration-with-room)

* Khi database của bạn có sự thay đổi về cấu trúc các bảng, sẽ rất đau đầu khi phải cập nhật thay đổi và giữ lại được các dữ liệu đang lưu trữ của người dùng.
* Trong Room, nếu có sự thay đổi về cấu trúc, thư viện sẽ gợi ý bạn tăng version của database lên ở trong cài đặt cấu hình của database. Nếu giữ nguyên version của database, ứng dụng của bạn sẽ bị **crash**.
* Nếu như thay đổi version nhưng lại không có migration cho version 1 lên 2, lúc này ứng dụng cũng **crash**. Lúc này hệ thống sẽ hiển thị thông báo cho bạn sử dụng phương thức **fallbackToDestructiveMigration()** để thay đổi hẳn database mới.

> Nhưng nếu làm theo thì database sẽ bị xóa hoàn toàn và không còn dữ liệu người dùng. Vì vậy chúng ta phải tìm cách để vừa cập nhật ứng dụng lại vừa có thể giữ lại được database cũ.

* Để giữ nguyên dữ liệu của người dùng, chúng ta cần tạo ra một migration giữa các phiên bản. Nếu schema không có sự thay đổi gì, chúng ta chỉ cần tạo ra một migration trống, lúc này dữ liệu sẽ được dữ lại:
* Trong trường hợp migration với schema thay đổi ít, sẽ phải dùng một số lệnh của SQL như **ALTER** để thay đổi tên hoặc thêm cột mới.
* Nếu thay đổi những cấu trúc phức tạp của schema, bạn cần thao tác thêm việc copy từ bảng cũ sang bảng mới bằng lệnh SQL được viết trong migration:
* Việc cập nhật theo từng phiên bản diễn ra theo trình tự trên, nhưng Room cũng hỗ trợ việc migration nhiều version lại với nhau, ví dụ ở đây chúng ta thực hiện việc migration từ version 1 tới 4 bằng tổng hợp một migration chung cho nhiều thay đổi như sau:

```
database = Room.databaseBuilder(context.getApplicationContext(),
        UsersDatabase.class, "Sample.db")
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_1_4)
        .build();
```

## [Room with RxJava](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#room-with-rxjava)

* Các truy vấn của Room thường trả về qua **LiveData** hoặc **RxJava** như `Maybe`, `Single`, `Flowable` là các truy vấn có thể quan sát được. Chúng cho phép bạn nhận được sự thay đổi của database một cách nhanh nhất để có thể cập nhật UI.
* Nếu đã làm việc với RxJava quen thì thư viện Room cũng hỗ trợ kết hợp với Rx để tạo ra các quan sát đối với dữ liệu trong ứng dụng.
* Việc trả về theo Rx, có thể sử dụng cho **@Insert**, **@Update**, **@Delete** để xử lý các quan sát đối với từng loại.
* Như bình thường thì phương thức **@Query** là một cách gọi đồng bộ(synchronous). Chúng ta cần phải gọi nhiều lần khi có sự thay đổi của dữ liệu. Vì vậy nêu sử dụng RxJava để có thể quan sát được những thay đổi của database. Room hỗ trợ thực hiện những cuộc gọi không đồng bộ(asynchronous) đối với Rx.
* Nếu bạn lo lắng về việc các thread, Room hỗ trợ quan sát trên nhiều luồng khác nhau tùy thuộc vào việc bạn sử dụng thread nào để xử lý.

1. Maybe

* Khi truy vấn không có gì, nó sẽ chạy vào **onComplete**.
* Khi có user trong database, nó sẽ chạy vào **onSuccess**.
* Nếu người dùng cập nhật database sau khi complete, không có gì xảy ra.

2. Single

* Khi không có user hoặc là không trả về kết quả, single sẽ kích hoạt **onError()**.
* Khi có user trong database, nó sẽ kích hoạt **onSuccess()**. 
* Nếu người dùng cập nhật sau khi single complete, không có gì xảy ra.

3. Flowable/Observable

* Khi không có dữ liệu hoặc truy vấn trả về trống, nó sẽ không gọi hàm gì kể cả **onNext()** và **onError()**. 
* Khi có người dùng trong database, sẽ kích hoạt **onNext()**. 
* Mỗi khi người dùng cập nhật dữ liệu, đối tượng này sẽ tự động phát ra cho phép người dùng cập nhật giao diện.

## [Room with Time](https://github.com/oHoangNgocThai/RoomAndSQLiteSample/blob/master/app/Documents.md#room-with-time)

* Nếu bạn có nhu cầu phải lưu trữ và truy xuất một số loại ngày giờ. Room không cung cấp cho bạn bất kì hỗ trợ nào, nhưng thay vào đó lại cung cấp annotation **@TypeConverter** để map giữa những đối tượng phức tạp về dạng mà Room có thể hiểu được và ngược lại.
* Dưới đây sử dụng class Converters để có thể convert time ở dạng Long về Date và ngược lại:

* Sau đó khai báo các lớp Converter cần thiết vào trong Database Room lúc khởi tạo:

```
@Database(entities = arrayOf(User::class), version = 1)
@TypeConverters(TimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
}
```

* Bây giờ bạn có thể sử dụng đối tượng Date như là một đối tượng được Room hiểu được:

```
@Entity
data class User(private var birthday: Date?)
```

* Lấy ra các đối tượng thỏa mãn điều kiện **Date** tương tự như với kiểu dữ liệu bình thường như sau:

```
@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE birthday BETWEEN :from AND :to")
    fun findUsersBornBetweenDates(from: Date, to: Date): List<User>
}
```
