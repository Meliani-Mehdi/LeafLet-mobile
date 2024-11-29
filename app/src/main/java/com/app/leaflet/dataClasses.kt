import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import java.time.Year

@Entity(tableName = "classes")
data class UnivClass(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val specialty: String = "",
    val level: String = "",
    val year: Int = Year.now().value
)

@Entity(
    tableName = "groups",
    foreignKeys = [ForeignKey(
        entity = UnivClass::class,
        parentColumns = ["id"],
        childColumns = ["univClassId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Group(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val type: String = "TD",
    val year: Int = Year.now().value,
    val univClassId: Int?
)
