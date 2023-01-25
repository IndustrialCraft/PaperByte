package com.github.industrialcraft.paperbyte.common.net;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.github.industrialcraft.netx.MessageRegistry;
import com.github.industrialcraft.paperbyte.common.util.IStreamSerializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ServerCollisionsDebugPacket {
    public final ArrayList<RenderData> data;
    public ServerCollisionsDebugPacket(ArrayList<RenderData> data) {
        this.data = data;
    }
    public ServerCollisionsDebugPacket(DataInputStream stream) throws IOException {
        int size = stream.readInt();
        this.data = new ArrayList<>(size);
        for(int i = 0;i < size;i++){
            this.data.add(fromStream(stream));
        }
    }
    public void toStream(DataOutputStream stream) throws IOException {
        stream.writeInt(data.size());
        for(var e : data){
            stream.writeByte(getType(e.getType()));
            e.toStream(stream);
        }
    }
    public static MessageRegistry.MessageDescriptor<ServerCollisionsDebugPacket> createDescriptor(){
        return new MessageRegistry.MessageDescriptor<>(ServerCollisionsDebugPacket.class, stream -> new ServerCollisionsDebugPacket(new DataInputStream(stream)), (obj, stream) -> obj.toStream(new DataOutputStream(stream)));
    }
    public static byte getType(Shape.Type type){
        return switch (type){
            case Circle -> 0;
            case Edge -> 1;
            case Polygon -> 2;
            case Chain -> 3;
        };
    }
    private static RenderData fromStream(DataInputStream stream) throws IOException {
        int type = stream.readByte();
        return switch(type){
            case 0 -> ServerCollisionRenderDataCircle.fromStream(stream);
            case 1 -> ServerCollisionRenderDataEdge.fromStream(stream);
            case 2 -> ServerCollisionRenderDataPolygon.fromStream(stream);
            case 3 -> ServerCollisionRenderDataChain.fromStream(stream);
            default -> throw new IllegalStateException("unknown collision render type");
        };
    }
    public interface RenderData extends IStreamSerializable {
        Transform getTransform();
        Shape.Type getType();
    }
    private static Transform transformFromStream(DataInputStream stream) throws IOException {
        Transform transform = new Transform();
        transform.vals[0] = stream.readFloat();
        transform.vals[1] = stream.readFloat();
        transform.vals[2] = stream.readFloat();
        transform.vals[3] = stream.readFloat();
        return transform;
    }
    public static void transformToStream(DataOutputStream stream, Transform transform) throws IOException {
        stream.writeFloat(transform.vals[0]);
        stream.writeFloat(transform.vals[1]);
        stream.writeFloat(transform.vals[2]);
        stream.writeFloat(transform.vals[3]);
    }
    public record ServerCollisionRenderDataCircle(Transform transform, Vector2 pos, float radius) implements RenderData {
        public static ServerCollisionRenderDataCircle fromStream(DataInputStream stream) throws IOException {
            Transform transform = transformFromStream(stream);
            return new ServerCollisionRenderDataCircle(transform, new Vector2(stream.readFloat(), stream.readFloat()), stream.readFloat());
        }
        public void toStream(DataOutputStream stream) throws IOException {
            transformToStream(stream, transform);
            stream.writeFloat(pos.x);
            stream.writeFloat(pos.y);
            stream.writeFloat(radius);
        }
        @Override
        public Transform getTransform() {
            return transform;
        }
        @Override
        public Shape.Type getType() {
            return Shape.Type.Circle;
        }
    }
    public record ServerCollisionRenderDataEdge(Transform transform, Vector2 v1, Vector2 v2) implements RenderData {
        public static ServerCollisionRenderDataEdge fromStream(DataInputStream stream) throws IOException {
            Transform transform = transformFromStream(stream);
            return new ServerCollisionRenderDataEdge(transform, new Vector2(stream.readFloat(), stream.readFloat()), new Vector2(stream.readFloat(), stream.readFloat()));
        }
        public void toStream(DataOutputStream stream) throws IOException {
            transformToStream(stream, transform);
            stream.writeFloat(v1.x);
            stream.writeFloat(v1.y);
            stream.writeFloat(v2.x);
            stream.writeFloat(v2.y);
        }
        @Override
        public Transform getTransform() {
            return transform;
        }
        @Override
        public Shape.Type getType() {
            return Shape.Type.Edge;
        }
    }
    public record ServerCollisionRenderDataPolygon(Transform transform, Vector2[] vertices) implements RenderData {
        public static ServerCollisionRenderDataPolygon fromStream(DataInputStream stream) throws IOException {
            Transform transform = transformFromStream(stream);
            int vertexCount = stream.readInt();
            Vector2[] vertices = new Vector2[vertexCount];
            for(int i = 0;i < vertexCount;i++){
                vertices[i] = new Vector2(stream.readFloat(), stream.readFloat());
            }
            return new ServerCollisionRenderDataPolygon(transform, vertices);
        }
        public void toStream(DataOutputStream stream) throws IOException {
            transformToStream(stream, transform);
            int vertexCount = vertices.length;
            stream.writeInt(vertexCount);
            for(int i = 0;i < vertexCount;i++){
                stream.writeFloat(vertices[i].x);
                stream.writeFloat(vertices[i].y);
            }
        }
        @Override
        public Transform getTransform() {
            return transform;
        }
        @Override
        public Shape.Type getType() {
            return Shape.Type.Polygon;
        }

        @Override
        public String toString() {
            return "ServerCollisionRenderDataPolygon{" +
                    "transform={" + transform.getPosition() + "," + transform.getRotation() +
                    "}, vertices=" + Arrays.stream(vertices).map(Vector2::toString).collect(Collectors.joining(",")) +
                    '}';
        }
    }
    public record ServerCollisionRenderDataChain(Transform transform, Vector2[] vertices) implements RenderData {
        public static ServerCollisionRenderDataChain fromStream(DataInputStream stream) throws IOException {
            Transform transform = transformFromStream(stream);
            int vertexCount = stream.readInt();
            Vector2[] vertices = new Vector2[vertexCount];
            for(int i = 0;i < vertexCount;i++){
                vertices[i] = new Vector2(stream.readFloat(), stream.readFloat());
            }
            return new ServerCollisionRenderDataChain(transform, vertices);
        }
        public void toStream(DataOutputStream stream) throws IOException {
            transformToStream(stream, transform);
            int vertexCount = vertices.length;
            for(int i = 0;i < vertexCount;i++){
                stream.writeFloat(vertices[i].x);
                stream.writeFloat(vertices[i].y);
            }
        }
        @Override
        public Transform getTransform() {
            return transform;
        }
        @Override
        public Shape.Type getType() {
            return Shape.Type.Chain;
        }
    }
}
