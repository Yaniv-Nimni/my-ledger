package unit.ledger.dataaccess
import unit.ledger.domain.AccountDomain.{Entity, IdUpdater}
import zio._

object Repository {

  type RepositoryEnv[A <: Entity] = Has[Repository[A]]

  trait Repository[A <: Entity] {
    def add(entry: A)(implicit ev: IdUpdater[A]): UIO[A]
    def get(id: Int): Task[A]
    def getAll: Task[Map[Int, A]]
    def getWhere(predicate: A => Boolean): UIO[Map[Int, A]]
    def update(id: Int, entry: A): UIO[A]
  }

  object RepositoryImpl {
    def initialize[A <: Entity](implicit tag: Tag[A]): ZLayer[Any, Nothing, RepositoryEnv[A]] = (for {
      map <- Ref.make(Map[Int, A]())
    } yield RepositoryImpl[A](map)).toLayer
  }

  case class RepositoryImpl[A <: Entity] private (table: Ref[Map[Int, A]]) extends Repository[A] {

    private def getNextId: UIO[Int] = for {
      map <- table.get
      id  = map.size + 1
    } yield id

    def add(entry: A)(implicit ev: IdUpdater[A]): UIO[A] =
      for {
        map     <- table.get
        id      <- getNextId
        newAcc  = ev.updateId(entry, id)
        newMap  = map + (id -> newAcc)
        _       <- table.set(newMap)
      } yield newAcc

    def update(id: Int, entry: A): UIO[A] = for {
      map     <- table.get
      newMap  = map + (id -> entry)
      _       <- table.set(newMap)
    } yield entry

    def get(id: Int): Task[A] = for {
      map   <- table.get
      data  <- ZIO(map(id))
    } yield data

    def getAll: Task[Map[Int, A]] = for {
      map <- table.get
    } yield map

    def getWhere(predicate: A => Boolean): UIO[Map[Int, A]] = for {
      map         <- table.get
      filteredMap = map.view.filter(kvp => predicate(kvp._2)).toMap
    } yield filteredMap
  }
}
