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
package jp.co.golorp.emarf.servlet;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.constants.MessageKeys;
import jp.co.golorp.emarf.exception.ApplicationError;
import jp.co.golorp.emarf.exception.SystemError;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.util.DateUtil;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * サービス時間外の時間帯に合致するか確認するフィルタ
 *
 * @author oukuf@golorp
 */
public class OutOfServiceFilter implements Filter {

	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(OutOfServiceFilter.class);

	/** リソースバンドル */
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(OutOfServiceFilter.class.getSimpleName());

	@Override
	public void init(final FilterConfig fConfig) throws ServletException {
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {

		LOG.debug("filter start.");

		String methodName = RequestUtil.getPathMethodName(request);
		LOG.debug("methodName = " + methodName);

		LOG.debug("ymdhms = " + DateUtil.getYmdHms());

		for (String key : BUNDLE.keySet()) {

			String type = null;
			String boStop = null;
			String eoStop = null;
			String[] values = BUNDLE.getString(key).split("\t");
			for (String value : values) {
				if (StringUtil.isNotBlank(value)) {
					if (type == null) {
						type = value;
					} else if (boStop == null) {
						boStop = value;
					} else if (eoStop == null) {
						eoStop = value;
					} else {
						break;
					}
				}
			}

			if (type.equals("R") && methodName.equalsIgnoreCase(EmarfServlet.METHOD_GET)) {
				// 照会

				if (isBetween(boStop, eoStop)) {
					throw new SystemError(MessageKeys.ERRORS_STATE_REFER, key);
				}

			} else if (type.equals("W") && (methodName.equalsIgnoreCase(EmarfServlet.METHOD_POST)
					|| methodName.equalsIgnoreCase(EmarfServlet.METHOD_PUT)
					|| methodName.equalsIgnoreCase(EmarfServlet.METHOD_DELETE))) {
				// 追加・更新・削除

				if (isBetween(boStop, eoStop)) {
					throw new ApplicationError(MessageKeys.ERRORS_STATE_REGIST, key);
				}
			}
		}

		chain.doFilter(request, response);

		LOG.debug("filter end.");
	}

	/** 曜日 */
	private static final int CRON_W = 4;

	/** 月 */
	private static final int CRON_M = 3;

	/** 日 */
	private static final int CRON_D = 2;

	/** 時 */
	private static final int CRON_H = 1;

	/** 分 */
	private static final int CRON_N = 0;

	/** 組み合わせて評価時に使用する係数 */
	private static final int K = 100;

	/** DAY_OF_WEEKとクーロン式の曜日差異を吸収する際に剰余を求める係数 */
	private static final int SURPLUS = 7;

	/**
	 * システム時間が開始設定と終了設定の範囲内か
	 * <ol>
	 * <li>指定がない - 1
	 * <li>指定がある
	 * <ol>
	 * <li>合致しない - 2
	 * <li>合致する - 3
	 * </ol>
	 * </ol>
	 *
	 * @param boStop
	 *            開始設定
	 * @param eoStop
	 *            終了設定
	 * @return 期間内か
	 */
	protected static boolean isBetween(final String boStop, final String eoStop) {

		// 各指定地を取得
		String[] boStops = boStop.replaceAll(" +", " ").split(" ");
		String[] eoStops = eoStop.replaceAll(" +", " ").split(" ");

		// 各指定があるか
		boolean isW = !boStops[CRON_W].equals("*") && !eoStops[CRON_W].equals("*");
		boolean isM = !boStops[CRON_M].equals("*") && !eoStops[CRON_M].equals("*");
		boolean isD = !boStops[CRON_D].equals("*") && !eoStops[CRON_D].equals("*");
		boolean isH = !boStops[CRON_H].equals("*") && !eoStops[CRON_H].equals("*");
		boolean isN = !boStops[CRON_N].equals("*") && !eoStops[CRON_N].equals("*");

		// 各評価値・各開始値・各終了値
		int w = 0;
		int bow = 0;
		int eow = 0;
		if (isW) {
			w = DateUtil.getDayOfWeek() * K * K * K * K;
			// DAY_OF_WEEKとcronの差異を吸収するため7の剰余を求める
			// java 日:1, 月:2, 火:3, 水:4, 木:5, 金:6, 土:7
			// cron 日:0, 月:1, 火:2, 水:3, 木:4, 金:5, 土:6, 日:7
			bow = (Integer.parseInt(boStops[CRON_W]) % SURPLUS + 1) * K * K * K * K;
			eow = (Integer.parseInt(eoStops[CRON_W]) % SURPLUS + 1) * K * K * K * K;
		}

		int m = 0;
		int bom = 0;
		int eom = 0;
		if (isM) {
			m = DateUtil.getM() * K * K * K;
			bom = Integer.parseInt(boStops[CRON_M]) * K * K * K;
			eom = Integer.parseInt(eoStops[CRON_M]) * K * K * K;
		}

		int d = 0;
		int bod = 0;
		int eod = 0;
		if (isD) {
			d = DateUtil.getD() * K * K;
			bod = Integer.parseInt(boStops[CRON_D]) * K * K;
			eod = Integer.parseInt(eoStops[CRON_D]) * K * K;
		}

		int h = 0;
		int boh = 0;
		int eoh = 0;
		if (isH) {
			h = DateUtil.getH() * K;
			boh = Integer.parseInt(boStops[CRON_H]) * K;
			eoh = Integer.parseInt(eoStops[CRON_H]) * K;
		}

		int n = 0;
		int bon = 0;
		int eon = 0;
		if (isN) {
			n = DateUtil.getN();
			bon = Integer.parseInt(boStops[CRON_N]);
			eon = Integer.parseInt(eoStops[CRON_N]);
		}

		// 曜日指定あり
		if (isW) {

			if (!isW(isM, isD, isH, isN, w, bow, eow, m, bom, eom, d, bod, eod, h, boh, eoh, n, bon, eon)) {
				return false;
			}

		} else {

			// 分指定のみ
			if (!isM && !isD && !isH && isN && !isBetween(n, bon, eon)) {
				return false;
			}

			// 時指定のみ
			if (!isM && !isD && isH && !isN && !isBetween(h, boh, eoh)) {
				return false;
			}

			// 日指定のみ
			if (!isM && isD && !isH && !isN && !isBetween(d, bod, eod)) {
				return false;
			}

			// 月指定のみ
			if (isM && !isD && !isH && !isN && !isBetween(m, bom, eom)) {
				return false;
			}

			// 時分指定
			if (!isM && !isD && isH && isN && !isBetween(h + n, boh + bon, eoh + eon)) {
				return false;
			}

			// 日分指定
			if (!isM && isD && !isH && isN && (!isBetween(d, bod, eod) || !isBetween(n, bon, eon))) {
				return false;
			}

			// 月分指定
			if (isM && !isD && !isH && isN && (!isBetween(m, bom, eom) || !isBetween(n, bon, eon))) {
				return false;
			}

			// 日時指定
			if (!isM && isD && isH && !isN && (!isBetween(d, bod, eod) || !isBetween(h, boh, eoh))) {
				return false;
			}

			// 月時指定
			if (isM && !isD && isH && !isN && (!isBetween(m, bom, eom) || !isBetween(h, boh, eoh))) {
				return false;
			}

			// 月日指定
			if (isM && isD && !isH && !isN && !isBetween(m + d, bom + bod, eom + eod)) {
				return false;
			}

			// 日時分指定
			if (!isM && isD && isH && isN && (!isBetween(d, bod, eod) || !isBetween(h + n, boh + bon, eoh + eon))) {
				return false;
			}

			// 月日時指定
			if (isM && isD && isH && !isN && (!isBetween(m + d, bom + bod, eom + eod) || !isBetween(h, boh, eoh))) {
				return false;
			}

			// 月時分指定（2月3月の深夜00:00～早朝06:00など）
			if (isM && !isD && isH && isN && (!isBetween(m, bom, eom) || !isBetween(h + n, boh + bon, eoh + eon))) {
				return false;
			}

			// 月日分指定
			if (isM && isD && !isH && isN && (!isBetween(m + d, bom + bod, eom + eod) || !isBetween(n, bon, eon))) {
				return false;
			}

			// 月日時分指定（12/29の02:00～01/04の06:00など）
			if (isM && isD && isH && isN && !isBetween(m + d + h + n, bom + bod + boh + bon, eom + eod + eoh + eon)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @param isM
	 *            isM
	 * @param isD
	 *            isD
	 * @param isH
	 *            isH
	 * @param isN
	 *            isN
	 * @param w
	 *            w
	 * @param bow
	 *            bow
	 * @param eow
	 *            eow
	 * @param m
	 *            m
	 * @param bom
	 *            bom
	 * @param eom
	 *            eom
	 * @param d
	 *            d
	 * @param bod
	 *            bod
	 * @param eod
	 *            eod
	 * @param h
	 *            h
	 * @param boh
	 *            boh
	 * @param eoh
	 *            eoh
	 * @param n
	 *            n
	 * @param bon
	 *            bon
	 * @param eon
	 *            eon
	 * @return boolean
	 */
	private static boolean isW(final boolean isM, final boolean isD, final boolean isH, final boolean isN, final int w,
			final int bow, final int eow, final int m, final int bom, final int eom, final int d, final int bod,
			final int eod, final int h, final int boh, final int eoh, final int n, final int bon, final int eon) {

		// 曜日指定のみ
		if (!isM && !isD && !isH && !isN && !isBetween(w, bow, eow)) {
			return false;
		}

		// 曜月指定
		if (isM && !isD && !isH && !isN && (!isBetween(w, bow, eow) || !isBetween(m, bom, eom))) {
			return false;
		}

		// 曜日指定
		if (!isM && isD && !isH && !isN && (!isBetween(w, bow, eow) || !isBetween(d, bod, eod))) {
			return false;
		}

		// 曜時指定
		if (!isM && !isD && isH && !isN && (!isBetween(w, bow, eow) || !isBetween(h, boh, eoh))) {
			return false;
		}

		// 曜分指定
		if (!isM && !isD && !isH && isN && (!isBetween(w, bow, eow) || !isBetween(n, bon, eon))) {
			return false;
		}

		// 曜月日指定
		if (isM && isD && !isH && !isN && (!isBetween(w, bow, eow) || !isBetween(m + d, bom + bod, eom + eod))) {
			return false;
		}

		// 曜時分指定（土曜深夜02:00～月曜早朝06:00など）
		if (!isM && !isD && isH && isN && !isBetween(w + h + n, bow + boh + bon, eow + eoh + eon)) {
			return false;
		}

		// 曜日分指定
		if (!isM && isD && !isH && isN
				&& (!isBetween(w, bow, eow) || !isBetween(d, bod, eod) || !isBetween(n, bon, eon))) {
			return false;
		}

		// 曜月分指定
		if (isM && !isD && !isH && isN
				&& (!isBetween(w, bow, eow) || !isBetween(m, bom, eom) || !isBetween(n, bon, eon))) {
			return false;
		}

		// 曜日時指定
		if (!isM && isD && isH && !isN
				&& (!isBetween(w, bow, eow) || !isBetween(d, bod, eod) || !isBetween(h, boh, eoh))) {
			return false;
		}

		// 曜月時指定
		if (isM && !isD && isH && !isN
				&& (!isBetween(w, bow, eow) || !isBetween(m, bom, eom) || !isBetween(h, boh, eoh))) {
			return false;
		}

		// 曜日時分指定
		if (!isM && isD && isH && isN
				&& (!isBetween(w, bow, eow) || !isBetween(d, bod, eod) || !isBetween(h + n, boh + bon, eoh + eon))) {
			return false;
		}

		// 曜月時分指定
		if (isM && !isD && isH && isN
				&& (!isBetween(w, bow, eow) || !isBetween(m, bom, eom) || !isBetween(h + n, boh + bon, eoh + eon))) {
			return false;
		}

		// 曜月日分指定
		if (isM && isD && !isH && isN
				&& (!isBetween(w, bow, eow) || !isBetween(m + d, bom + bod, eom + eod) || !isBetween(n, bon, eon))) {
			return false;
		}

		// 曜月日時指定
		if (isM && isD && isH && !isN
				&& (!isBetween(w, bow, eow) || !isBetween(m + d, bom + bod, eom + eod) || !isBetween(h, boh, eoh))) {
			return false;
		}

		// 曜月日時分指定
		if (isM && isD && isH && isN && (!isBetween(w, bow, eow) || !isBetween(m + d, bom + bod, eom + eod)
				|| !isBetween(h + n, boh + bon, eoh + eon))) {
			return false;
		}

		return true;
	}

	/**
	 * @param i
	 *            評価値
	 * @param b
	 *            開始値
	 * @param e
	 *            終了値
	 * @return 評価値が開始値と終了値の範囲に合致するか
	 */
	private static boolean isBetween(final int i, final int b, final int e) {
		// 「評価値が開始値と終了値の範囲内」か
		// 「終了値が開始値より小さい場合は、評価値が開始より大きいか終了値より小さいのいずれか」
		return (b <= i && i <= e) || (e < b && (b <= i || i <= e));
	}

	@Override
	public void destroy() {
	}

}
