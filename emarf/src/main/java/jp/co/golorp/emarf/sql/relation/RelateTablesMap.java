/*
 * 「Apache License, Version 2.0」のライセンスが適用されます。
 * 当ファイルを使用する場合はこのライセンスに従ってください。
 *
 * ライセンスの全文は「http://www.apache.org/licenses/LICENSE-2.0」にあります。
 *
 * 適用法または書面による同意が必要な場合を除き、
 * ライセンスに基づいて配布されるソフトウェアは、
 * 明示的または黙示的にいかなる種類の保証や条件もなく「現状有姿」で配布されています。
 *
 * ライセンスに基づいて許可および制限を規定する特定の言語については上記ライセンスを参照してください。
 */
package jp.co.golorp.emarf.sql.relation;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 関連先モデル名ごとに関連テーブル情報のリストを管理するMap
 * <dl>
 * <dt>K
 * <dd>関連先モデル名
 * <dt>V
 * <dd>{@link RelateColumnMap}のリスト<br>
 * （同一モデルを複数回参照することがあるため）
 * </dl>
 *
 * @author oukuf@golorp
 */
public class RelateTablesMap extends LinkedHashMap<String, List<RelateColumnMap>> {

}
