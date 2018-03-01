package pt.um.tf.lab0.lab0srv

import io.atomix.catalyst.concurrent.SingleThreadContext
import io.atomix.catalyst.serializer.Serializer
import io.atomix.catalyst.transport.Address
import io.atomix.catalyst.transport.Connection
import io.atomix.catalyst.transport.netty.NettyTransport
import pt.um.tf.lab0.lab0mes.Message
import pt.um.tf.lab0.lab0mes.Reply
import java.util.concurrent.CompletableFuture


fun main(args : Array<String>) {
    val me = Address("localhost", 22556)
    val t = NettyTransport()
    val sr = Serializer()
    val tc = SingleThreadContext("srv-%d", sr)
    val acc = Account()
    tc.execute({t.server().listen(me, {handlers(it,acc)})}).join()
    while(readLine() == null);
    tc.close()
    t.close()
    println("I'm here")
}

fun handlers(connection: Connection, acc: Account) {
    connection.handler<Message,
                       CompletableFuture<Message>>(Message::class.java,
                                                   handleOp(connection, acc))
}

fun handleOp(conn: Connection, acc: Account) : (Message) -> Unit {
    return {
        println("Handling connection")
        when (it.op) {
                0 -> conn.send(Reply(it.op, true, acc.balance))
                1 -> conn.send(Reply(it.op, acc.movement(it.mov), 0))
                else -> throw IllegalArgumentException("Not recognized Message")
        }
    }
}

