
case class Using[T <: {def close(): Unit}](resource: T) {
  def apply[U](block: T => U): U = {
    try {
      block(resource)
    } finally {
      resource.close()
    }
  }
}

case class DbConnection(url: String) {
  def close(): Unit = ()
  def runQuery(s: String): String = ""
}


object Hello {
  def main(args: Array[String]): Unit = {
    Using(DbConnection("url")) { conn =>
      conn.runQuery("query")
    }
  }
}


